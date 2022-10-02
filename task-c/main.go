package main

import (
	"fmt"
	"github.com/lucsky/cuid"
	"math"
	"math/rand"
	"os"
	"os/signal"
	"sync"
	"syscall"
	"time"
)

type Road struct {
	a, b string
	cost int
}

var (
	lock       sync.RWMutex
	towns      []string
	townsLimit = 3
	roads      []Road
)

func priceChanger() {
	for {
		delay := time.Duration(rand.Intn(4) + 6)
		time.Sleep(delay * time.Second)

		lock.Lock()
		for i, x := range roads {
			x.cost = rand.Intn(100)
			fmt.Printf("Road %d repriced: %s <-> %s = %d\n", i, x.a, x.b, x.cost)
		}
		lock.Unlock()
	}
}

func removeRoad(p int) {
	x := roads[p]
	roads = append(roads[:p], roads[p+1:]...)
	fmt.Printf("Road deleted: %s <-> %s = %d, len: %d\n", x.a, x.b, x.cost, len(roads))
}

func roadChanger() {
	for {
		delay := time.Duration(rand.Intn(2) + 3)
		time.Sleep(delay * time.Second)

		lock.Lock()
		tl := len(towns)
		rl := len(roads)
		roadsLimit := tl * (tl - 1) / 2
		if rl == 0 || (rl < roadsLimit && rand.Float64() > 0.3) {
			if tl >= 2 {
				// create new road
				for {
					a := towns[rand.Intn(tl)]
					b := towns[rand.Intn(tl)]
					if a == b {
						continue
					}
					if a > b {
						a, b = b, a
					}
					for _, x := range roads {
						if a == x.a && b == x.b {
							continue
						}
					}
					cost := rand.Intn(100)
					roads = append(roads, Road{a, b, cost})
					fmt.Printf("Road added: %s <-> %s = %d, len: %d\n", a, b, cost, len(roads))
					break
				}
			}
		} else {
			// remove random road
			p := rand.Intn(rl)
			removeRoad(p)
		}
		lock.Unlock()
	}
}

func createTown() {
	name := cuid.New()
	towns = append(towns, name)
	fmt.Printf("Town added: %s, len: %d\n", name, len(towns))
}

func townChanger() {
	// add initial towns
	createTown()
	createTown()

	for {
		delay := time.Duration(rand.Intn(3) + 6)
		time.Sleep(delay * time.Second)

		lock.Lock()
		tl := len(towns)
		if tl == 0 || (tl < townsLimit && rand.Float64() > 0.3) {
			// create new town
			createTown()
		} else {
			// remove random town
			p := rand.Intn(tl)
			x := towns[p]
			towns = append(towns[:p], towns[p+1:]...)
			fmt.Printf("Town deleted: %s, len: %d\n", x, len(roads))
			// remove related roads
			for i := 0; i < len(roads); i += 1 {
				r := roads[i]
				if x == r.a || x == r.b {
					removeRoad(i)
					i -= 1
				}
			}
		}
		lock.Unlock()
	}
}

func pathfinder() {
	for {
		delay := time.Duration(rand.Intn(3) + 6)
		time.Sleep(delay * time.Second)

		tl := len(towns)
		if tl < 2 {
			continue
		}

		lock.Lock()
		var ai, bi int
		for {
			ai = rand.Intn(tl)
			bi = rand.Intn(tl)
			if ai != bi {
				break
			}
		}
		if ai > bi {
			ai, bi = bi, ai
		}

		toVisit := make([]bool, tl)
		dist := make([]int, tl)
		for i := 0; i < tl; i += 1 {
			toVisit[i] = true
			dist[i] = math.MaxInt
		}
		dist[ai] = 0

		for i := 0; i < tl; i += 1 {
			// select town
			curTownI, curTownDist := -1, math.MaxInt
			for k := 0; k < tl; k += 1 {
				d := dist[k]
				if toVisit[k] && curTownDist > d {
					curTownI = k
					curTownDist = d
				}
			}
			if curTownI == -1 {
				break
			}
			toVisit[curTownI] = false
			curTown := towns[curTownI]

			// calc related roads
			for _, r := range roads {
				var toTown string
				// if related road
				if curTown == r.a {
					toTown = r.b
				} else if curTown == r.b {
					toTown = r.a
				} else {
					continue
				}

				var toTownI int
				for ti, tn := range towns {
					if toTown == tn {
						toTownI = ti
						break
					}
				}

				if dist[toTownI] > (curTownDist + r.cost) {
					dist[toTownI] = curTownDist + r.cost
				}
			}
		}

		a, b := towns[ai], towns[bi]
		res := dist[bi]
		if res == math.MaxInt {
			fmt.Printf("Dist: %s -> %s = INF\n", a, b)
		} else {
			fmt.Printf("Dist: %s -> %s = %d\n", a, b, res)
		}

		lock.Unlock()
	}
}

func await() {
	exitSignal := make(chan os.Signal)
	signal.Notify(exitSignal, syscall.SIGINT, syscall.SIGTERM)
	<-exitSignal
}

func main() {
	go priceChanger()
	go roadChanger()
	go townChanger()
	go pathfinder()
	await()
}
