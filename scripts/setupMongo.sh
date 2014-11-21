sudo yum -y update


echo "[MongoDB]
name=MongoDB Repository
baseurl=http://downloads-distro.mongodb.org/repo/redhat/os/x86_64
gpgcheck=0
enabled=1" | sudo tee -a /etc/yum.repos.d/mongodb.repo

sudo yum install -y mongodb-org-server mongodb-org-shell mongodb-org-tools

sudo mkdir -p /data/db/
sudo mkdir /log /journal

echo "logpath=/var/log/mongodb/mongod.log

logappend=true

fork=true

dbpath=/var/lib/mongo

pidfilepath=/var/run/mongodb/mongod.pid" > /etc/mongod.conf


sudo service mongod start