PATH := ./valkey-git/src:${PATH}
STUNNEL_BIN := $(shell which stunnel)

define VALKEY1_CONF
daemonize yes
protected-mode no
port 6379
requirepass foobared
user acljedis on allcommands allkeys >fizzbuzz
pidfile /tmp/valkey1.pid
logfile /tmp/valkey1.log
save ""
appendonly no
enable-module-command yes
client-output-buffer-limit pubsub 256k 128k 5
extended-redis-compatibility yes
endef

define VALKEY2_CONF
daemonize yes
protected-mode no
port 6380
requirepass foobared
pidfile /tmp/valkey2.pid
logfile /tmp/valkey2.log
save ""
appendonly no
extended-redis-compatibility yes
endef

define VALKEY3_CONF
daemonize yes
protected-mode no
port 6381
requirepass foobared
masterauth foobared
pidfile /tmp/valkey3.pid
logfile /tmp/valkey3.log
save ""
appendonly no
extended-redis-compatibility yes
endef

define VALKEY4_CONF
daemonize yes
protected-mode no
port 6382
requirepass foobared
masterauth foobared
pidfile /tmp/valkey4.pid
logfile /tmp/valkey4.log
save ""
appendonly no
slaveof localhost 6381
extended-redis-compatibility yes
endef

define VALKEY5_CONF
daemonize yes
protected-mode no
port 6383
requirepass foobared
masterauth foobared
pidfile /tmp/valkey5.pid
logfile /tmp/valkey5.log
save ""
appendonly no
slaveof localhost 6379
extended-redis-compatibility yes
endef

define VALKEY6_CONF
daemonize yes
protected-mode no
port 6384
requirepass foobared
masterauth foobared
pidfile /tmp/valkey6.pid
logfile /tmp/valkey6.log
save ""
appendonly no
extended-redis-compatibility yes
endef

define VALKEY7_CONF
daemonize yes
protected-mode no
port 6385
requirepass foobared
masterauth foobared
pidfile /tmp/valkey7.pid
logfile /tmp/valkey7.log
save ""
appendonly no
slaveof localhost 6384
extended-redis-compatibility yes
endef

define VALKEY8_CONF
daemonize yes
protected-mode no
port 6386
pidfile /tmp/valkey8.pid
logfile /tmp/valkey8.log
save ""
appendonly no
maxmemory-policy allkeys-lfu
extended-redis-compatibility yes
endef

define VALKEY9_CONF
daemonize yes
protected-mode no
port 6387
user default off
user acljedis on allcommands allkeys >fizzbuzz
pidfile /tmp/valkey9.pid
logfile /tmp/valkey9.log
save ""
appendonly no
client-output-buffer-limit pubsub 256k 128k 5
extended-redis-compatibility yes
endef

define VALKEY10_CONF
daemonize yes
protected-mode no
port 6388
pidfile /tmp/valkey10.pid
logfile /tmp/valkey10.log
save ""
appendonly no
extended-redis-compatibility yes
endef

define VALKEY11_CONF
daemonize yes
protected-mode no
port 6389
pidfile /tmp/valkey11.pid
logfile /tmp/valkey11.log
save ""
appendonly no
replicaof localhost 6388
extended-redis-compatibility yes
endef

define VALKEY12_CONF
daemonize yes
protected-mode no
port 6390
pidfile /tmp/valkey12.pid
logfile /tmp/valkey12.log
save ""
appendonly no
extended-redis-compatibility yes
replica-enable-redirect yes
endef

define VALKEY13_CONF
daemonize yes
protected-mode no
port 6391
pidfile /tmp/valkey13.pid
logfile /tmp/valkey13.log
save ""
appendonly no
replicaof localhost 6390
extended-redis-compatibility yes
replica-enable-redirect yes
endef

# SENTINELS
define VALKEY_SENTINEL1
port 26379
daemonize yes
protected-mode no
sentinel monitor mymaster 127.0.0.1 6379 1
sentinel auth-pass mymaster foobared
sentinel down-after-milliseconds mymaster 2000
sentinel failover-timeout mymaster 120000
sentinel parallel-syncs mymaster 1
pidfile /tmp/sentinel1.pid
logfile /tmp/sentinel1.log
endef

define VALKEY_SENTINEL2
port 26380
daemonize yes
protected-mode no
sentinel monitor mymaster 127.0.0.1 6381 1
sentinel auth-pass mymaster foobared
sentinel down-after-milliseconds mymaster 2000
sentinel parallel-syncs mymaster 1
sentinel failover-timeout mymaster 120000
pidfile /tmp/sentinel2.pid
logfile /tmp/sentinel2.log
endef

define VALKEY_SENTINEL3
port 26381
daemonize yes
protected-mode no
sentinel monitor mymasterfailover 127.0.0.1 6384 1
sentinel auth-pass mymasterfailover foobared
sentinel down-after-milliseconds mymasterfailover 2000
sentinel failover-timeout mymasterfailover 120000
sentinel parallel-syncs mymasterfailover 1
pidfile /tmp/sentinel3.pid
logfile /tmp/sentinel3.log
endef

define VALKEY_SENTINEL4
port 26382
daemonize yes
protected-mode no
sentinel monitor mymaster 127.0.0.1 6381 1
sentinel auth-pass mymaster foobared
sentinel down-after-milliseconds mymaster 2000
sentinel parallel-syncs mymaster 1
sentinel failover-timeout mymaster 120000
pidfile /tmp/sentinel4.pid
logfile /tmp/sentinel4.log
endef

define VALKEY_SENTINEL5
port 26383
daemonize yes
protected-mode no
user default off
user sentinel on allcommands allkeys allchannels >foobared
sentinel monitor aclmaster 127.0.0.1 6387 1
sentinel auth-user aclmaster acljedis
sentinel auth-pass aclmaster fizzbuzz
sentinel down-after-milliseconds aclmaster 2000
sentinel failover-timeout aclmaster 120000
sentinel parallel-syncs aclmaster 1
pidfile /tmp/sentinel5.pid
logfile /tmp/sentinel5.log
endef

# CLUSTER VALKEY NODES
define VALKEY_CLUSTER_NODE1_CONF
daemonize yes
protected-mode no
requirepass cluster
port 7379
cluster-node-timeout 15000
pidfile /tmp/valkey_cluster_node1.pid
logfile /tmp/valkey_cluster_node1.log
save ""
appendonly no
cluster-enabled yes
cluster-config-file /tmp/valkey_cluster_node1.conf
extended-redis-compatibility yes
endef

define VALKEY_CLUSTER_NODE2_CONF
daemonize yes
protected-mode no
requirepass cluster
port 7380
cluster-node-timeout 15000
pidfile /tmp/valkey_cluster_node2.pid
logfile /tmp/valkey_cluster_node2.log
save ""
appendonly no
cluster-enabled yes
cluster-config-file /tmp/valkey_cluster_node2.conf
extended-redis-compatibility yes
endef

define VALKEY_CLUSTER_NODE3_CONF
daemonize yes
protected-mode no
requirepass cluster
port 7381
cluster-node-timeout 15000
pidfile /tmp/valkey_cluster_node3.pid
logfile /tmp/valkey_cluster_node3.log
save ""
appendonly no
cluster-enabled yes
cluster-config-file /tmp/valkey_cluster_node3.conf
extended-redis-compatibility yes
endef

define VALKEY_CLUSTER_NODE4_CONF
daemonize yes
protected-mode no
requirepass cluster
port 7382
cluster-node-timeout 15000
pidfile /tmp/valkey_cluster_node4.pid
logfile /tmp/valkey_cluster_node4.log
save ""
appendonly no
cluster-enabled yes
cluster-config-file /tmp/valkey_cluster_node4.conf
extended-redis-compatibility yes
endef

define VALKEY_CLUSTER_NODE5_CONF
daemonize yes
protected-mode no
requirepass cluster
port 7383
cluster-node-timeout 15000
pidfile /tmp/valkey_cluster_node5.pid
logfile /tmp/valkey_cluster_node5.log
save ""
appendonly no
cluster-enabled yes
cluster-config-file /tmp/valkey_cluster_node5.conf
extended-redis-compatibility yes
endef

# STABLE CLUSTER VALKEY NODES
# The structure of this cluster is not changed by the tests!
define VALKEY_STABLE_CLUSTER_NODE1_CONF
daemonize yes
protected-mode no
requirepass cluster
port 7479
cluster-node-timeout 15000
pidfile /tmp/valkey_stable_cluster_node1.pid
logfile /tmp/valkey_stable_cluster_node1.log
save ""
appendonly no
cluster-enabled yes
cluster-config-file /tmp/valkey_stable_cluster_node1.conf
extended-redis-compatibility yes
endef

define VALKEY_STABLE_CLUSTER_NODE2_CONF
daemonize yes
protected-mode no
requirepass cluster
port 7480
cluster-node-timeout 15000
pidfile /tmp/valkey_stable_cluster_node2.pid
logfile /tmp/valkey_stable_cluster_node2.log
save ""
appendonly no
cluster-enabled yes
cluster-config-file /tmp/valkey_stable_cluster_node2.conf
extended-redis-compatibility yes
endef

define VALKEY_STABLE_CLUSTER_NODE3_CONF
daemonize yes
protected-mode no
requirepass cluster
port 7481
cluster-node-timeout 15000
pidfile /tmp/valkey_stable_cluster_node3.pid
logfile /tmp/valkey_stable_cluster_node3.log
save ""
appendonly no
cluster-enabled yes
cluster-config-file /tmp/valkey_stable_cluster_node3.conf
extended-redis-compatibility yes
endef

# UDS VALKEY NODES
define VALKEY_UDS
daemonize yes
protected-mode no
port 0
pidfile /tmp/valkey_uds.pid
logfile /tmp/valkey_uds.log
unixsocket /tmp/valkey_uds.sock
unixsocketperm 777
save ""
appendonly no
extended-redis-compatibility yes
endef

# UNAVAILABLE VALKEY NODES
define VALKEY_UNAVAILABLE_CONF
daemonize yes
protected-mode no
port 6400
pidfile /tmp/valkey_unavailable.pid
logfile /tmp/valkey_unavailable.log
save ""
appendonly no
endef

#STUNNEL
define STUNNEL_CONF
cert = src/test/resources/private.pem
pid = /tmp/stunnel.pid
[valkey_1]
accept = 127.0.0.1:6390
connect = 127.0.0.1:6379
[valkey_3]
accept = 127.0.0.1:16381
connect = 127.0.0.1:6381
[valkey_4]
accept = 127.0.0.1:16382
connect = 127.0.0.1:6382
[valkey_9]
accept = 127.0.0.1:16387
connect = 127.0.0.1:6387
[valkey_cluster_1]
accept = 127.0.0.1:8379
connect = 127.0.0.1:7379
[valkey_cluster_2]
accept = 127.0.0.1:8380
connect = 127.0.001:7380
[valkey_cluster_3]
accept = 127.0.0.1:8381
connect = 127.0.001:7381
[valkey_cluster_4]
accept = 127.0.0.1:8382
connect = 127.0.0.1:7382
[valkey_cluster_5]
accept = 127.0.0.1:8383
connect = 127.0.0.1:7383
[valkey_sentinel_5]
accept = 127.0.0.1:36383
connect = 127.0.0.1:26383
endef

export VALKEY1_CONF
export VALKEY2_CONF
export VALKEY3_CONF
export VALKEY4_CONF
export VALKEY5_CONF
export VALKEY6_CONF
export VALKEY7_CONF
export VALKEY8_CONF
export VALKEY9_CONF
export VALKEY10_CONF
export VALKEY11_CONF
export VALKEY12_CONF
export VALKEY13_CONF
export VALKEY_SENTINEL1
export VALKEY_SENTINEL2
export VALKEY_SENTINEL3
export VALKEY_SENTINEL4
export VALKEY_SENTINEL5
export VALKEY_CLUSTER_NODE1_CONF
export VALKEY_CLUSTER_NODE2_CONF
export VALKEY_CLUSTER_NODE3_CONF
export VALKEY_CLUSTER_NODE4_CONF
export VALKEY_CLUSTER_NODE5_CONF
export VALKEY_STABLE_CLUSTER_NODE1_CONF
export VALKEY_STABLE_CLUSTER_NODE2_CONF
export VALKEY_STABLE_CLUSTER_NODE3_CONF
export VALKEY_UDS
export VALKEY_UNAVAILABLE_CONF
export STUNNEL_CONF
export STUNNEL_BIN


ifndef STUNNEL_BIN
    SKIP_SSL := !SSL*,
endif
export SKIP_SSL

start: stunnel cleanup compile-module
	echo "$$VALKEY1_CONF" | valkey-server -
	echo "$$VALKEY2_CONF" | valkey-server -
	echo "$$VALKEY3_CONF" | valkey-server -
	echo "$$VALKEY4_CONF" | valkey-server -
	echo "$$VALKEY5_CONF" | valkey-server -
	echo "$$VALKEY6_CONF" | valkey-server -
	echo "$$VALKEY7_CONF" | valkey-server -
	echo "$$VALKEY8_CONF" | valkey-server -
	echo "$$VALKEY9_CONF" | valkey-server -
	echo "$$VALKEY10_CONF" | valkey-server -
	echo "$$VALKEY11_CONF" | valkey-server -
	echo "$$VALKEY12_CONF" | valkey-server -
	echo "$$VALKEY13_CONF" | valkey-server -
	echo "$$VALKEY_SENTINEL1" > /tmp/sentinel1.conf && valkey-server /tmp/sentinel1.conf --sentinel
	@sleep 0.5
	echo "$$VALKEY_SENTINEL2" > /tmp/sentinel2.conf && valkey-server /tmp/sentinel2.conf --sentinel
	@sleep 0.5
	echo "$$VALKEY_SENTINEL3" > /tmp/sentinel3.conf && valkey-server /tmp/sentinel3.conf --sentinel
	@sleep 0.5
	echo "$$VALKEY_SENTINEL4" > /tmp/sentinel4.conf && valkey-server /tmp/sentinel4.conf --sentinel
	@sleep 0.5
	echo "$$VALKEY_SENTINEL5" > /tmp/sentinel5.conf && valkey-server /tmp/sentinel5.conf --sentinel
	@sleep 0.5
	echo "$$VALKEY_CLUSTER_NODE1_CONF" | valkey-server -
	echo "$$VALKEY_CLUSTER_NODE2_CONF" | valkey-server -
	echo "$$VALKEY_CLUSTER_NODE3_CONF" | valkey-server -
	echo "$$VALKEY_CLUSTER_NODE4_CONF" | valkey-server -
	echo "$$VALKEY_CLUSTER_NODE5_CONF" | valkey-server -
	echo "$$VALKEY_STABLE_CLUSTER_NODE1_CONF" | valkey-server -
	echo "$$VALKEY_STABLE_CLUSTER_NODE2_CONF" | valkey-server -
	echo "$$VALKEY_STABLE_CLUSTER_NODE3_CONF" | valkey-server -
	echo "$$VALKEY_UDS" | valkey-server -
	echo "$$VALKEY_UNAVAILABLE_CONF" | valkey-server -
	@sleep 0.5
	valkey-cli -a cluster --cluster create 127.0.0.1:7479 127.0.0.1:7480 127.0.0.1:7481 --cluster-yes
cleanup:
	- rm -vf /tmp/valkey_cluster_node*.conf 2>/dev/null
	- rm dump.rdb appendonly.aof - 2>/dev/null

stunnel:
	@if [ -e "$$STUNNEL_BIN" ]; then\
	    echo "$$STUNNEL_CONF" | stunnel -fd 0;\
	fi

stop:
	kill `cat /tmp/valkey1.pid`
	kill `cat /tmp/valkey2.pid`
	kill `cat /tmp/valkey3.pid`
	kill `cat /tmp/valkey4.pid`
	kill `cat /tmp/valkey5.pid`
	kill `cat /tmp/valkey6.pid`
	kill `cat /tmp/valkey7.pid`
	kill `cat /tmp/valkey8.pid`
	kill `cat /tmp/valkey9.pid`
	kill `cat /tmp/valkey10.pid`
	kill `cat /tmp/valkey11.pid`
	kill `cat /tmp/valkey12.pid`
	kill `cat /tmp/valkey13.pid`
	kill `cat /tmp/sentinel1.pid`
	kill `cat /tmp/sentinel2.pid`
	kill `cat /tmp/sentinel3.pid`
	kill `cat /tmp/sentinel4.pid`
	kill `cat /tmp/sentinel5.pid`
	kill `cat /tmp/valkey_cluster_node1.pid` || true
	kill `cat /tmp/valkey_cluster_node2.pid` || true
	kill `cat /tmp/valkey_cluster_node3.pid` || true
	kill `cat /tmp/valkey_cluster_node4.pid` || true
	kill `cat /tmp/valkey_cluster_node5.pid` || true
	kill `cat /tmp/valkey_stable_cluster_node1.pid`
	kill `cat /tmp/valkey_stable_cluster_node2.pid`
	kill `cat /tmp/valkey_stable_cluster_node3.pid`
	kill `cat /tmp/valkey_uds.pid` || true
	kill `cat /tmp/stunnel.pid` || true
	[ -f /tmp/valkey_unavailable.pid ] && kill `cat /tmp/valkey_unavailable.pid` || true
	rm -f /tmp/sentinel1.conf
	rm -f /tmp/sentinel2.conf
	rm -f /tmp/sentinel3.conf
	rm -f /tmp/sentinel4.conf
	rm -f /tmp/sentinel5.conf
	rm -f /tmp/valkey_cluster_node1.conf
	rm -f /tmp/valkey_cluster_node2.conf
	rm -f /tmp/valkey_cluster_node3.conf
	rm -f /tmp/valkey_cluster_node4.conf
	rm -f /tmp/valkey_cluster_node5.conf
	rm -f /tmp/valkey_stable_cluster_node1.conf
	rm -f /tmp/valkey_stable_cluster_node2.conf
	rm -f /tmp/valkey_stable_cluster_node3.conf

test: | start mvn-test stop

mvn-test:
	mvn -Dtest=${SKIP_SSL}${TEST} clean compile test

package: | start mvn-package stop

mvn-package:
	mvn clean package

deploy: | start mvn-deploy stop

mvn-deploy:
	mvn clean deploy

format:
	mvn java-formatter:format

release: | start mvn-release stop

mvn-release:
	mvn release:clean
	mvn release:prepare
	mvn release:perform -DskipTests

system-setup:
	[ ! -e valkey-git ] && git clone https://github.com/valkey-io/valkey.git --branch unstable --single-branch valkey-git || true
	$(MAKE) -C valkey-git clean
	$(MAKE) -C valkey-git

compile-module:
	gcc -shared -o /tmp/testmodule.so -fPIC src/test/resources/testmodule.c


.PHONY: test
