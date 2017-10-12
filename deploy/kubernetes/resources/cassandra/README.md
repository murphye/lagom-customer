# Cassandra with StatefulSets on Kubernetes

This directory contains the source code and Kubernetes manifests for Cassandra
deployment with StatefulSets tutorial.

Follow this tutorial at https://kubernetes.io/docs/tutorials/stateful-application/cassandra/.

# Create Cassandra Cluster
kubectl create -f cassandra-service.yaml
kubectl create -f cassandra-statefulset.yaml

# Check Status
kubectl get statefulset cassandra
kubectl get pods -l="app=cassandra"

Note: It can take up to ten minutes for all three Pods to deploy.

## Get stdout logs
kubectl logs cassandra-1

## Check cluster status
kubectl exec cassandra-0 -- nodetool status

## Scale Cassandra Cluster
kubectl scale statefulsets cassandra --replicas=4

## Delete Cassandra StatefulSet

grace=$(kubectl get po cassandra-0 -o=jsonpath='{.spec.terminationGracePeriodSeconds}')   && kubectl delete statefulset -l app=cassandra   && echo "Sleeping $grace"   && sleep $grace   && kubectl delete pvc -l app=cassandra

## Delete Persistent Volumes

kubectl get pv
kubectl delete pv (the claim ID

## Azure Persistent Volume Storage Class

https://kubernetes.io/docs/concepts/storage/persistent-volumes/#new-azure-disk-storage-class-starting-from-v172

