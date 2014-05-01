# webnikki Starting Guide

[![Build Status](https://secure.travis-ci.org/mahata/webnikki.png?branch=master)](https://travis-ci.org/mahata/webnikki)

## What's webnikki

nikki (日記) is a word which means diary. webnikki is a service to publish your nikki on web :)

## Requirements

* Vagrant 1.4.3 (or above)
* Ansible 1.5.4 (or above)
* Play Framework 2.1.5

Following are assuming that you're using either Mac (OSX) or Linux.

## How to setup

```
(local) git clone https://github.com/mahata/webnikki.git
(local) cd webnikki
(local) vagrant up
(local) vagrant provision
(local) vagrant ssh
(vm) cd ~/src
(vm) play "run -Dconfig.file=conf/dev.conf"
```

Add following line to `/etc/hosts`

```
127.0.0.1	local.webnikki.com
```

You'll be access to http://local.webnikki.com:9000/ now!

## How to run tests

```
(local) vagrant up
(local) vagrant provision  # If it's not provisioned yet
(local) vagrant ssh
(vm) cd ~/src
(vm) play test
```

## How to hack

You can change Scala code under `PATH/TO/webnikki/src` directly since it's NFS mounted.

### How to hack with IntelliJ

Provided you have Play Framework 2.1.5 in your `$PATH` and yoru IntelliJ has Scala plugin, run following commands:

```
(local) cd /PATH/TO/webnikki/src
(local) play
(play) idea with-sources=yes
```

Once it's done, do followings:

* Open IntelliJ
* Open Project -> /PATH/TO/webnikki/src
* Happy Hacking!
