# create/update resources
kubectl --context=minikube apply -f ./postgres.yaml

# In order for the service to reach the statefulset, the following should
# be true:
#   statefulset.spec.selector.matchLabels.app  == service.spec.selector.app
#   statefulset.spec.selector.matchLabels.role == service.spec.selector.role

# give the server some time to start up
# ...

# test the connection from outside the cluster
psql --host=$(minikube ip) \
     --port=$(minikube service postgres --url --format={{.Port}}) \
     --username=postgres \
     --dbname=postgres

# test the connection from within the cluster
url=$(kubectl --context=minikube get service postgres \
              --output=jsonpath='{.spec.clusterIP}:{.spec.ports[0].port}')
kubectl --context=minikube run pgbox --image=postgres:9.6 \
    --rm -it --restart=Never -- \
    bash -c "read &&
             psql --host=${url%:*} --port=${url#*:} \
                  --username=postgres --dbname=postgres \
                  --command='SELECT refobjid FROM pg_depend LIMIT 1'"

# remove resources
kubectl --context=minikube delete -f ./postgres.yaml --ignore-not-found
# Data will survive the above operation, and be available next time you revive postgres.
