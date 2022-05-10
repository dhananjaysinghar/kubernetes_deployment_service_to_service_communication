# kubernetes_deployment_service_to_service_communication


Steps:
0. Start docker and minikube in local machine.
1. Create a spring boot application.
2. Create a docker image and push to hub.
3. Create a namespace in kubernetes.
4. Create 2files deployment.yaml and services.yml
5. Create Deployment in kubernetes.
6. Create Service in kubernetes.
7. Create a load balancer with service in kubernetes.
8. Run minikube tunnel command to expose those lb's to localhost 



## Main Commands:
~~~
minikube start
minikube stop
minikube tunnel  -> we need to keep this in  running state after creation of lb
kubectl get all -n test-namespace

kubectl get all -n test-namespace
kubectl config set-context --current --namespace=test-namespace

Internal cluster url template:
http://{{service-name}}.{{namespace-name}}.svc.cluster.local:{{portnumber}}

~~~

### DOC-SVC:
~~~
docker build -f Dockerfile -t doc-svc .
docker tag doc-svc dhananjayasinghar/doc-svc
docker push dhananjayasinghar/doc-svc:latest
kubectl create namespace test-namespace
kubectl config set-context --current --namespace=test-namespace
kubectl create deployment deployment-doc-svc --image=dhananjayasinghar/doc-svc:latest --dry-run=client -o=yaml > deployment.yaml
kubectl create service clusterip service-doc-svc --tcp=8080:8080 --dry-run=client -o=yaml > service.yaml
kubectl create -f deployment.yaml -n test-namespace
kubectl create -f service.yaml -n test-namespace
kubectl get all -n test-namespace
kubectl expose deployment deployment-doc-svc --type=LoadBalancer --name=lb-doc-svc  --port=8080
minikube tunnel
kubectl logs pod/deployment-doc-svc-7d9b8b7fc6-vkxkm -n test-namespace
~~~


### IMPORT-SVC:
~~~
docker build -f Dockerfile -t import-svc .
docker tag import-svc dhananjayasinghar/import-svc
docker push dhananjayasinghar/import-svc:latest
docker run -p 9090:9090 dhananjayasinghar/import-svc:latest
kubectl create deployment deployment-import-svc --image=dhananjayasinghar/import-svc:latest --dry-run=client -o=yaml > deployment.yaml
kubectl create service clusterip service-import-svc --tcp=9090:9090 --dry-run=client -o=yaml > service.yaml
kubectl create -f deployment.yaml -n test-namespace
kubectl create -f service.yaml -n test-namespace
kubectl get all -n test-namespace
kubectl expose deployment deployment-import-svc --type=LoadBalancer --name=lb-import-svc  --port=9090
minikube tunnel

Note: Run minikube tunnel at end to expose the lb to localhost
Note: We can call service to another service over lb-name (lb-doc-svc)
~~~

### Output:
~~~
C:\Users\Dhananjay\Desktop\Kubernetes_Spring_app\import-svc>kubectl get all -n test-namespace
NAME                                         READY   STATUS    RESTARTS        AGE
pod/deployment-doc-svc-7d9b8b7fc6-vkxkm      1/1     Running   1 (7m58s ago)   89m
pod/deployment-import-svc-6d4b8cc5d5-rghtn   1/1     Running   1 (7m58s ago)   10m

NAME                         TYPE           CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
service/lb-doc-svc           LoadBalancer   10.99.35.160     127.0.0.1     8080:31589/TCP   44m
service/lb-import-svc        LoadBalancer   10.100.44.137    127.0.0.1     9090:31450/TCP   8m59s
service/service-doc-svc      ClusterIP      10.104.199.148   <none>        8080/TCP         89m
service/service-import-svc   ClusterIP      10.105.208.147   <none>        9090/TCP         10m

NAME                                    READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/deployment-doc-svc      1/1     1            1           89m
deployment.apps/deployment-import-svc   1/1     1            1           10m

NAME                                               DESIRED   CURRENT   READY   AGE
replicaset.apps/deployment-doc-svc-7d9b8b7fc6      1         1         1       89m
replicaset.apps/deployment-import-svc-6d4b8cc5d5   1         1         1       10m
~~~

Userful Commands:
~~~
kubectl port-forward service/service-import-svc 9090:9090 -n test-namespace
kubectl logs deployment-import-svc-6d4b8cc5d5-ncfz6
kubectl exec -it deployment-import-svc-6d4b8cc5d5-kv9n9 bash
minikube service --url service-import-svc --namespace test-namespace
kubectl delete ns test-namespace
kubectl get pods --all-namespaces
kubectl apply -f deployment.yaml -n test-namespace
kubectl get all -n test-namespace
kubectl get pods --output=wide
kubectl delete pods load-generator
kubectl get services -o wide
kubectl describe services app-doc-svc
minikube addons enable ingress
kubectl expose deployment deployment-doc-svc --type=LoadBalancer --name=test-namespace  --port=8080
kubectl get all -n test-namespace
kubectl logs pod/app-doc-svc-7967ffbb9c-xrjvv -n test-namespace
kubectl delete ns test-namespace
kubectl get svc pp-doc-svc -n test-namespace
kubectl patch svc app-doc-svc -n test-namespace -p '{"spec": {"type": "LoadBalancer", "externalIPs":["<public ip>"]}}'
~~~

### Create a linux machine:
~~~
kubectl run -it --rm test_linux --image=mcr.microsoft.com/aks/fundamental/base-ubuntu:v0.0.11
~~~

## INGRESS related
~~~
minikube addons enable ingress
  --> Automatically starts the k8s Nginx implementation of Ingress Controller.
kubectl get pod -n ingress-nginx

kubectl get ns
kubectl get pod -n test-namespace
kubectl apply -f ingress.yaml
kubectl get ingress --watch
kubectl get ingress --all-namespaces
kubectl describe ingress
kubectl get ingress test-ingress
kubectl get ingresses test-ingress
kubectl get service -n ingress-nginx
kubectl delete ingresses test-ingress

kubectl exec -it deployment-import-svc-6d4b8cc5d5-rghtn bash


curl --location --request GET 'http://docs.test.com/docs'


kubectl get service -n ingress-nginx  ingress-nginx-controller
kubectl patch service ingress-nginx-controller -n kube-system --patch "$(cat ingress-nginx-svc-patch.yaml)"

kubectl get deployments --all-namespaces
kubectl get ing


 annotations:
    kubernetes.io/ingress.class: nginx
    nginx.ingress.kubernetes.io/enable-cors: "true"
    nginx.ingress.kubernetes.io/cors-allow-methods: "PUT, GET, POST, DELETE, OPTIONS"
    nginx.ingress.kubernetes.io/cors-allow-credentials: "true"
    nginx.ingress.kubernetes.io/cors-allow-origin: "http://docs.test.com, http://origin-site.com, https://example.org:1199

kubectl get all -n test-namespace
kubectl get ing
kubectl describe ingress
~~~

#### ingress.yaml
~~~
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: test-ingress
  namespace: test-namespace
  annotations:
    kubernetes.io/ingress.class: nginx
#    nginx.ingress.kubernetes.io/whitelist-source-range: "27.110.30.45, 68.50.85.421"
    nginx.ingress.kubernetes.io/server-snippet: |
            set $agentflag 0;
            if ($http_user_agent ~* "(PostmanRuntime|outbound|import-service|doc-service)" ){
                set $agentflag 1;
            } 
            if ( $agentflag = 0 ) {
                return 403;
            }
        

spec:
  tls:
    - hosts:
        - docs.test.com
      secretName: tls-docs-test-com
  rules:
    - host: "*.test.com"
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: lb-doc-svc
                port:
                  number: 8080
