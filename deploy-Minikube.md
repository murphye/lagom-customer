## Running Lagom in Minikube

### Prerequisites

* [Maven](https://maven.apache.org/)
* [Docker](https://www.docker.com/)
* [Minikube](https://kubernetes.io/docs/getting-started-guides/minikube/)

### Recommended Reading

* [Deploying Lagom Microservices on Kubernetes](https://developer.lightbend.com/guides/lagom-kubernetes-k8s-deploy-microservices/)


### Deploy Lagom to Minikube
```
deploy/kubernetes/scripts/install --minikube --new-minikube --all
```
#### Output:
```
****************************
***  Deploying customer   ***
****************************
service "customerservice-akka-remoting" created
service "customerservice" created
statefulset "customerservice" created
waiting............
****************************
***  Deploying nginx     ***
****************************
ingress "customer-ingress" created
deployment "nginx-default-backend" created
service "nginx-default-backend" created
deployment "nginx-ingress-controller" created
service "nginx-ingress" created
waiting........................................................
NAME                                          READY     STATUS    RESTARTS   AGE
po/cassandra-0                                1/1       Running   0          5m
po/customerservice-0                          1/1       Running   0          1m
po/nginx-default-backend-1866436208-v8whf     1/1       Running   0          1m
po/nginx-ingress-controller-667491271-l4kh3   1/1       Running   0          1m

NAME                                CLUSTER-IP   EXTERNAL-IP   PORT(S)                      AGE
svc/cassandra                       10.0.0.57    <none>        9042/TCP                     5m
svc/customerservice                 None         <none>        9000/TCP                     1m
svc/customerservice-akka-remoting   10.0.0.36    <none>        2551/TCP                     1m
svc/kubernetes                      10.0.0.1     <none>        443/TCP                      5m
svc/nginx-default-backend           10.0.0.58    <none>        80/TCP                       1m
svc/nginx-ingress                   10.0.0.207   <pending>     80:31387/TCP,443:30797/TCP   1m

NAME                           DESIRED   CURRENT   AGE
statefulsets/cassandra         1         1         5m
statefulsets/customerservice   1         1         1m

NAME                              DESIRED   CURRENT   UP-TO-DATE   AVAILABLE   AGE
deploy/nginx-default-backend      1         1         1            1           1m
deploy/nginx-ingress-controller   1         1         1            1           1m

NAME                                    DESIRED   CURRENT   READY     AGE
rs/nginx-default-backend-1866436208     1         1         1         1m
rs/nginx-ingress-controller-667491271   1         1         1         1m


Customer Service (HTTP): http://192.168.99.100:31387
Customer Service (HTTPS): https://192.168.99.100:30797
Kubernetes Dashboard: http://192.168.99.100:30000

```
#### Add a customer

```curl -H "Content-Type: application/json" -X POST -d '{"name": "Eric Murphy", "city": "San Francisco", "state": "CA", "zipCode": "94105"}' http://192.168.99.100:31387/customer```