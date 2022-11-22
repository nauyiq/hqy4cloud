FROM centos:centos7
ENV KUBECONFIG /home/config
RUN yum install -y podman && \
    sed -i 's/#mount_program/mount_program/g' /etc/containers/storage.conf && \
    sed -i 's/mountopt/#mountopt/g' /etc/containers/storage.conf
