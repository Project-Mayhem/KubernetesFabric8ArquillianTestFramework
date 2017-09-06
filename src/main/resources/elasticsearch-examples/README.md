Check this site out to get ELK to work in minikube:
https://microservices-demo.github.io/microservices-demo/deployment/kubernetes-minikube.html

Resolve internal elasticsearch service exposure using busybox image.  DNS lookup from there:

https://kubernetes.io/docs/concepts/services-networking/connect-applications-service/

kubectl run curl --image=radial/busyboxplus:curl -i --tty
nslookup elasticsearch-service

or, if running already

kubectl exec -it --name... /bin/sh/nslookup elasticsearch-service