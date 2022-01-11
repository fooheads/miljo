# miljo

A Clojure lib to read environments / configs.

Creates an environment by merging config data from different sources.
Reads in this order:

- The first `config.edn` found on the classpath
- The first `$env-name/config.edn` found on the classpath
- The first `$env-name/secrets.edn` found on the classpath
- enviromnent variables
- java system properties

A common scenario is to have an `env` folder (added to the classpath),
that contains a folder for each named environment. `:dev` config
would then be found under `env/dev/config.edn`.

To set which environment to use, set the ENV enviroment variable. `:dev`
is default if none is given.

If a schema is provided at env/config-schema.edn, that schema is validated
as a strict/closed schema. Keys are selected from the schema (root) form
and validated.


## License

Available under the terms of the Eclipse Public License 2.0, see `LICENSE`.
