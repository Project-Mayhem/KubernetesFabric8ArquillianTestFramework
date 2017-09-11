#!/bin/bash

kubectl replace configmaps logstash-configmap -f logstash-configmap.yaml
if [ $? -gt 1 ] ; then
  kubectl create -f logstash-configmap.yaml
fi

kubectl replace configmaps fio-configmap -f fio-configmap.yaml
if [ $? -gt 1 ] ; then
  kubectl create -f fio-configmap.yaml
fi

kubectl delete jobs fio-with-logstash-sidecar-job

kubectl create -f fio-with-logstash-sidecar-job.yaml

sleep 3

kubectl get pods






