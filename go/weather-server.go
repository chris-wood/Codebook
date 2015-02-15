package main

import (
	"encoding/json"
	"net/http"
	"strings"
	"fmt"
	"time"
)

type weatherData struct {
    Name string `json:"name"`
    Main struct {
        Kelvin float64 `json:"temp"`
    } `json:"main"`
}

type weatherProvider interface {
    temperature(city string) (float64, error) // interface for a weather provider
}

type multiWeatherProvider []weatherProvider

type openWeatherMap struct{} // a type of weather provider

type weatherUnderground struct { // another type of weather provider
    apiKey string
}

var mw = multiWeatherProvider{
    // openWeatherMap{},
    weatherUnderground{apiKey: "6d23a0dc5134e0fe"},
}

func main() {
	http.HandleFunc("/hello", helloWorld)
	http.HandleFunc("/weather/", weatherQuery)
	http.ListenAndServe(":8080", nil)
}

func helloWorld(w http.ResponseWriter, r *http.Request) {
	fmt.Println(r.URL.Path)
	w.Write([]byte("hello!"))
}

func weatherQuery(w http.ResponseWriter, r *http.Request) {
    fmt.Println("Querying!")

    begin := time.Now()
    city := strings.SplitN(r.URL.Path, "/", 3)[2]

    temp, err := mw.temperature(city)
    if err != nil {
    	fmt.Println("Error occurred... :-\\")
        http.Error(w, err.Error(), http.StatusInternalServerError)
        return
    }

    fmt.Println("Done querying, generating response")

    w.Header().Set("Content-Type", "application/json; charset=utf-8")
    json.NewEncoder(w).Encode(map[string]interface{}{
        "city": city,
        "temp": temp,
        "took": time.Since(begin).String(),
    })
}

func (w openWeatherMap) temperature(city string) (float64, error) {
	resp, err := http.Get("http://api.openweathermap.org/data/2.5/weather?q=" + city)
	if err != nil {
		return 0, err
	}

	defer resp.Body.Close()

	var data struct { // struct to guide the JSON response parser
        Main struct {
            Kelvin float64 `json:"temp"`
        } `json:"main"`
    }

	if err := json.NewDecoder(resp.Body).Decode(&data); err != nil {
		return 0, err
	}

	fmt.Println("openWeatherMap: %s: %.2f", city, data.Main.Kelvin)
	return data.Main.Kelvin, nil
}

func (w weatherUnderground) temperature(city string) (float64, error) {
	resp, err := http.Get("http://api.wunderground.com/api/" + w.apiKey + "/conditions/q/" + city + ".json")
	if err != nil {
		return 0, err
	}

	defer resp.Body.Close()

	var data struct {
		Observation struct {
			Celsuis float64 `json:"temp_c"`
		} `json:"current_observation"`
	}

	if err := json.NewDecoder(resp.Body).Decode(&data); err != nil {
		return 0, err
	}

	kelvin := data.Observation.Celsuis + 273.15
	fmt.Println("weatherUnderground: %s: %.2f", city, kelvin)
	return kelvin, nil
}

// Note: redundant -- unused, since we replaced the parameter list with the 
// multiWeatherProvider type that wraps up multiple providers
func temperature(city string, providers ...weatherProvider) (float64, error) {
	sum := 0.0
	for _, provider := range providers { // range returns index and instance
		k, err := provider.temperature(city)
		if err != nil {
			return 0, err
		}
		sum += k
	}

	// return the average temperature observed from all providers
	return sum / float64(len(providers)), nil 
}

func (w multiWeatherProvider) temperature(city string) (float64, error) {
	temps := make(chan float64, len(w))
	errors := make(chan error, len(w))

    for _, provider := range w {
    	go func(p weatherProvider) { // anonymous function here
        	k, err := provider.temperature(city)
        	if err != nil {
	            errors <- err
	            return
    	    }

        	temps <- k
        }(provider)
    }

    sum := 0
    for i := 0; i < len(w); i++ {
    	select {
    	case temp := <-temps:
    		sum += temp
    	case err := <-errs:
    		return 0, err
    	}
    }

    return sum / float64(len(w)), nil
}