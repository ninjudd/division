(ns ninjudd.division.link
  (:require [clojure.string :refer [lower-case split]]
            [clojure.set :refer [union]]
            [ninjudd.division :refer [<< >> all digit either exclude-trailing int* is-not many
                                      maybe one parse prefix scan-all str* string symbol-char 
                                      token transform verify with-consumed]]))

;; Top-level domains
(def original-tlds #{"com" "org" "net" "edu" "gov"})
(def country-tlds #{"ac" "af" "ag" "am" "ar" "as" "at" "au" "be" "bz" "ca" "cc" "ch" "cl" "cm" "co" "cx" "cz" "de" "dk" "ec" "es" "eu" "fm" "fr" "gd" "gg" "gl" "gr" "gs" "gy" "hn" "ht" "im" "in" "io" "is" "it" "je" "jp" "la" "lc" "li" "lt" "lu" "lv" "me" "mn" "ms" "mu" "mx" "my" "nl" "nu" "nz" "pe" "ph" "pl" "pm" "pt" "pw" "re" "sb" "sc" "se" "sg" "sh" "so" "sx" "tc" "tf" "tk" "tl" "to" "tv" "tw" "uk" "us" "vc" "vg" "wf" "ws" "yt" "za"})
(def generic-tlds #{"bar" "bid" "bio" "biz" "cab" "how" "ink" "kim" "moe" "nyc" "pub" "red" "soy" "tax" "tel" "uno" "vet" "wtf" "xyz" "asia" "band" "beer" "best" "bike" "blue" "buzz" "camp" "care" "casa" "cash" "city" "club" "cool" "diet" "fail" "farm" "fish" "fund" "gift" "guru" "haus" "help" "host" "immo" "info" "kiwi" "land" "life" "limo" "link" "menu" "mobi" "moda" "name" "pics" "pink" "qpon" "rest" "sarl" "scot" "sexy" "surf" "tips" "town" "toys" "wang" "wien" "wiki" "work" "yoga" "zone" "actor" "archi" "audio" "black" "build" "cards" "cheap" "click" "coach" "codes" "dance" "deals" "email" "gifts" "gives" "glass" "gripe" "guide" "horse" "house" "jetzt" "koeln" "lease" "legal" "loans" "media" "money" "ninja" "paris" "parts" "party" "photo" "pizza" "place" "press" "rehab" "reise" "shoes" "solar" "space" "today" "tokyo" "tools" "trade" "vegas" "vodka" "watch" "works" "world" "agency" "bayern" "berlin" "br.com" "camera" "career" "center" "church" "claims" "clinic" "coffee" "condos" "credit" "dating" "degree" "dental" "direct" "durban" "energy" "estate" "eu.com" "events" "expert" "futbol" "global" "gratis" "hiphop" "insure" "joburg" "juegos" "kaufen" "lawyer" "london" "maison" "market" "nagoya" "photos" "quebec" "reisen" "repair" "report" "ryukyu" "schule" "social" "supply" "tattoo" "tienda" "viajes" "villas" "vision" "voting" "voyage" "webcam" "academy" "auction" "capital" "careers" "cologne" "company" "cooking" "country" "cricket" "cruises" "dentist" "digital" "domains" "exposed" "finance" "fishing" "fitness" "flights" "florist" "forsale" "gallery" "guitars" "hamburg" "holiday" "hosting" "kitchen" "limited" "network" "okinawa" "recipes" "rentals" "science" "shiksha" "singles" "support" "surgery" "systems" "website" "attorney" "bargains" "boutique" "brussels" "builders" "business" "capetown" "catering" "cleaning" "clothing" "computer" "delivery" "democrat" "diamonds" "discount" "engineer" "exchange" "graphics" "holdings" "lighting" "memorial" "mortgage" "partners" "pictures" "plumbing" "property" "revrodeo" "services" "software" "supplies" "training" "ventures" "yokohama" "christmas" "community" "directory" "education" "equipment" "financial" "furniture" "institute" "marketing" "solutions" "vacations" "associates" "consulting" "foundation" "immobilien" "industries" "management" "properties" "republican" "restaurant" "technology" "university" "vlaanderen" "accountants" "blackfriday" "contractors" "engineering" "enterprises" "investments" "photography" "productions" "construction" "international"})
(def all-tlds (union original-tlds country-tlds generic-tlds))

(defn host-char []
  (either (symbol-char)
          (one \.)))

(defn path-char []
  (either (symbol-char)
          (token #{\. \~ \/ \= \& \! \* \' \( \) \; \: \@ \+ \$ \, \% \[ \]})))

(defn link-without-scheme []
  (let [path (str* (many (path-char)))]
    (-> (all (str* (many (host-char) 1))
             (maybe (>> (one \:) (int* (many (digit)))))
             (maybe (>> (one \/) path))
             (maybe (>> (one \?) path))
             (maybe (>> (one \#) path)))
        (transform (fn [[host port path query fragment]]
                     (->> {:host host
                           :port port
                           :path path
                           :query query
                           :fragment fragment}
                          (filter val)
                          (into {})))))))

(defn link-with-scheme []
  (-> (all (<< (str* (many (symbol-char) 1))
               (string "://"))
           (link-without-scheme))
      (transform (fn [[scheme link]]
                   (assoc link :scheme scheme)))))

(defn inferred-link []
  (-> (link-without-scheme)
      (verify (fn [link]
                (let [segments (split (:host link) #"\.")]
                  (and (< 1 (count segments))
                       (contains? all-tlds (last segments))))))))

(defn link []
  (-> (either (link-with-scheme) (inferred-link))
      (with-consumed)
      (transform (fn [[link url]]
                   (assoc link
                     :url (apply str (if (:scheme link)
                                       url
                                       (cons "http://" url))))))))

(def parse-link (parse link))

(defn scan-links [string]
  (let [scan (-> (link)
                 (exclude-trailing (-> (token #{\. \? \! \, \) \;})
                                       (many 1)))
                 (scan-all))]
    (->> (scan string)
         (first)
         (map (fn [item]
                (if (map? item)
                  item
                  (apply str item)))))))
