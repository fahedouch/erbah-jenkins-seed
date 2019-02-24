#! /bin/bash

# Script to execute on a fresh TMA3 Jenkins slave installation to make it work.
# Must be run as root.

if [ "$USER" != "root" ]
then
    echo "This script must be run as root"
    exit 1
fi

# Make sure the Outscale DNS servers are up
cat > /etc/dhcp/dhclient-enter-hooks <<EOF
#!/bin/sh
make_resolv_conf() {
    echo "doing nothing to resolv.conf"
}
EOF
chmod a+x /etc/dhcp/dhclient-enter-hooks

rm -rf /etc/resolv.conf
cat > /etc/resolv.conf <<EOF
search admin dv.admin
options single-request

nameserver 172.19.203.10
nameserver 172.19.203.11
EOF


##### Update packages
echo -e "\n\e[36m - Update packages\e[0m"
yum update -y

##### lsof #####
echo -e "\n\e[36m - lsof version\e[0m"
if ! [ -x "$(command -v lsof)" ];
then
    echo -e "\e[31m   Need to install lsof\e[0m\n"
    yum install -y lsof
fi

##### Git #####
echo -e "\n\e[36m - git version\e[0m"
if ! [ -x "$(command -v git)" ];
then
    echo -e "\e[31m   Need to install git\e[0m\n"
    yum install -y git
fi
git --version


##### Docker #####
# Docker itself
echo -e "\n\e[36m - docker version\e[0m"
if ! [ -x "$(command -v docker)" ];
then
    echo -e "\e[31m   Need to install docker\e[0m\n"
    # Add the certificate for dockreg
    wget http://osu.eu-west-2.outscale.com/public-share/OutscaleSAS.pem \
        -P /etc/pki/ca-trust/source/anchors/
    update-ca-trust extract
    systemctl restart docker

    yum install -y docker
    systemctl enable docker.service

    # It seems too easy to just have to start docker, we also have to manage
    # ourself to allow the jenkins user to use docker...
    groupadd docker
    gpasswd -a jenkins docker
    systemctl start docker.service
    systemctl status docker.service
fi
docker version

# Pip (required for Docker Compose)
echo -e "\n\e[36m - pip version\e[0m"
if ! [ -x "$(command -v pip)" ];
then
    echo -e "\e[31m   Need to install pip\e[0m\n"
    yum install -y python2-pip
fi
pip --version

# Docker Compose
echo -e "\n\e[36m - docker-compose version\e[0m"
if ! [ -x "$(command -v docker-compose)" ];
then
    echo -e "\e[31m   Need to install docker for running TINA v3 tests\e[0m\n"
    /usr/bin/pip install docker-compose
fi
docker-compose version

