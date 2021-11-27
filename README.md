# kubernetes_deployment_service_to_service_communication


## Main Commands:
~~~
minikube start
minikube stop
minikube tunnel  -> we need to keep this in  running state after creation of lb
kubectl get all -n test-namespace

kubectl get all -n test-namespace
kubectl config set-context --current --namespace=test-namespace

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
