(ns fooheads.miljo.a.core-test
  (:require
    [clojure.test :refer [deftest is]]
    [fooheads.miljo.a.core :as env]))


(deftest dev-env-test
  (env/setup-env! :dev)
  (is (= {:common "common"
          :override 8081
          :qualified/keyword "qualified"
          :only-in-dev "only-in-dev"
          :not-common "dev"
          :password "dev secret"}
         (env/get-env))))


(deftest prod-env-test
  (env/setup-env! :prod)
  (is (= {:common "common"
          :override 80
          :qualified/keyword "qualified"
          :only-in-prod "only-in-prod"
          :not-common "prod"
          :password "prod secret"}
         (env/get-env))))


(deftest bad-env-test
  (is (thrown-with-msg? Exception #"Invalid config" (env/setup-env! :bad))))

