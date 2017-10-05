#!/bin/bash

oc project meadowgate

oc delete pvc --all

oc create -f fio-pvc-ceph.yaml

oc replace configmaps logstash-configmap -f logstash-configmap.yaml
if [ $? -gt 1 ] ; then
  oc create -f logstash-configmap.yaml
fi

oc replace configmaps fio-configmap -f fio-configmap.yaml
if [ $? -gt 1 ] ; then
  oc create -f fio-configmap.yaml
fi

oc delete deployments fio-with-logstash-sidecar-deployment

oc delete deployments fio-deployment

oc create -f fio-deployment.yaml 

#oc create -f fio-with-logstash-sidecar-deployment.yaml

sleep 3

oc get pods








