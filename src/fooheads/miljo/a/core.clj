(ns fooheads.miljo.a.core
  "Creates an environment by merging config data from different sources.
  Reads in this order:

  - The first `config.edn` found on the classpath
  - The first `$env-name/config.edn` found on the classpath
  - enviromnent variables
  - java system properties

  A common scenario is to have an `env` folder (added to the classpath),
  that contains a folder for each named environment. `:dev` config
  would then be found under `env/dev/config.edn`.

  To set which environment to use, set the ENV enviroment variable.
  `:dev` is default if none is given."
  (:require
    [config.core]
    [malli.core :as m]
    [malli.error :as me]
    [malli.util :as mu]))


(defn get-env-name
  "Return the current ENV name, or `:dev` if not specified"
  []
  (keyword (or (System/getenv "ENV") :dev)))


(defn get-env
  "Returns the current environment map"
  []
  config.core/env)


(defn load-env
  "Generate a map of environment variables."
  [env-name & configs]
  (apply
    config.core/merge-maps
    (config.core/read-config-file "config.edn")                                 ; env/config.edn
    (config.core/read-config-file (format "%s/config.edn" (name env-name)))     ; env/dev/config.edn
    (config.core/read-config-file (format "%s/secrets.edn" (name env-name)))    ; env/dev/secrets.edn
    (config.core/read-system-env)                                               ; enviromnent variables
    (config.core/read-system-props)                                             ; java props
    {:env env-name}                                                             ; current env name
    configs))                                                                   ; Extra configs. Must be last, a slight bug in merge-maps


(defn- schema-keys [schema]
  (->>
    schema
    (rest)
    (mapv first)))


(defn- validate-env! [env]
  (if-let [schema (config.core/read-config-file "config-schema.edn")]
    (let [env (select-keys env (schema-keys schema))]
      (if (m/validate (mu/closed-schema schema) env)
        env
        (let [explanation (m/explain schema env)
              humanized (me/humanize explanation)]
          (throw (ex-info (str "Invalid config\n" humanized "\n") explanation)))))
    env))


(defn setup-env!
  "Setup environment map for a particular environment name"
  ([] (setup-env! (get-env-name)))

  ([env-name]
   (alter-var-root #'config.core/env (fn [_] (validate-env! (load-env env-name))))
   nil))

