# TD SAS : Rapport final

Tout d'abord, pour pouvoir faire les travaux pratiques demandés, nous avons besoin d'un environnement de travail sous linux.  
Nous avons donc crée une machine virtuelle à partir de VirtualBox.  

Ensuite, nous avons installé une Debian Minimal à partir du mini.iso fourni par notre encadrant sur cette machine virtuelle.  

Pendant l'installation, nous avons choisi un mot de passe administrateur **root** (ce mot de passe peut être changé à tout moment avec la commande **passwd**) et crée un utilisateur *lambda* (sans droits administrateur) avec son mot de passe.  

Une fois l'installation faite, le terminal de base du système récemment installé étant pas très pratique, il est préférable de se connecter à la nouvelle machine depuis notre propre terminal via **ssh**.  
Sans toucher au fichier de configuation de ssh de base, nous pouvons seulement nous connecter en tant que utilisateur *lambda*.

    kimmeng@shelby:~$ ssh root@192.168.0.30  
    root@192.168.0.30's password:  
    Permission denied, please try again.

Par conséquent, pour pouvoir nous connecter en tant que *root* (avec les droits administrateur), nous devons ajouter la ligne suivante dans le fichier **/etc/ssh/sshd_config**.  
    
    PermitRootLogin yes

Cette ligne permet à *root* de se connecter en SSH ou à un utilisateur *lambda* en **su** (super-utilisateur). Ainsi, après avoir ajouter cette ligne, nous redémarrons le service **ssh** via la commande :  

    /etc/init.d/ssh restart  

Une fois que le service **ssh** redémarré, nous pouvons enfin nous connecter en tant que *root*.


    kimmeng@shelby:~$ ssh root@192.168.0.30  
    root@192.168.0.30's password:   
    root@debian:~#  


**TP2** Partitions ?  
**TP3** SSH génération de clés


Nous allons maintenant procéder à la mise en place des conteneurs **lxc**. Tout d'abord, nous devons installer les paquets de **lxc** avec la commande (en tant que *root*) :  

    root@debian:~# apt-get install lxc lxctl lxc-tests lxc-templates  

Par défaut sur Debian la configuration réseau pour les conteneurs est désactivée, alors pour avoir du réseau dans nos conteneurs, nous devons mettre à jour le fichier de configuration de **lxc** comme ci-dessous:  

D'abord le fichier **/etc/lxc/default.conf** : 

    lxc.net.0.type = veth
    lxc.net.0.link = virbr0
    lxc.net.0.flags = up
    lxc.net.0.hwaddr = 00:16:3e:xx:xx:xx
    lxc.apparmor.profile = generated
    lxc.apparmor.allow_nesting = 1

Et mettre à **true** l'option **USE_LXC_BRIDGE** dans le fichier **/etc/default/lxc** :  
    
    USE_LXC_BRIDGE="true"  # overridden in lxc-net

Ensuite, nous pouvons créer notre premier conteneur **c1** avec la commande :  

    root@debian:~# lxc-create -n c1 -t download  

**-n** : pour spécifier le nom du conteneur  
**-t** : pour spécifier le template  

Nous précisons ensuite quel type de distribution nous voulons installer :

    Distribution: debian
    Release: buster
    Architecture: amd64  

Une fois que notre conteneur est installé, nous pouvons le voir avec **lxc-ls** qui permet de lister les conteneurs installés : 

    root@debian:~# lxc-ls
    c1  

Nous pouvons ensuite démarrer notre conteneur avec la commande :  
    
    root@debian:~# lxc-start -n c1 -d

Pour vérifier que le conteneur est bien démarré : 

    root@debian:~# lxc-info c1
    Name:           c1
    State:          RUNNING
    PID:            996
    IP:             10.0.3.16
    CPU use:        0.46 seconds
    BlkIO use:      21.55 MiB
    Memory use:     40.14 MiB
    KMem use:       4.50 MiB
    Link:           vethWH4YQY
     TX bytes:      1.62 KiB
     RX bytes:      1.87 KiB
    Total bytes:   3.50 KiB  

Le conteneur **c1** est configuré comme suit : 

    root@debian:~# cat /var/lib/lxc/c1/config 
    ...
    # Distribution configuration
    lxc.include = /usr/share/lxc/config/common.conf
    lxc.arch = linux64

    # Container specific configuration
    lxc.apparmor.profile = generated
    lxc.apparmor.allow_nesting = 1
    lxc.rootfs.path = dir:/var/lib/lxc/c1/rootfs
    lxc.uts.name = c1

    # Network configuration
    lxc.net.0.type = veth
    lxc.net.0.link = lxcbr0
    lxc.net.0.flags = up
    lxc.net.0.hwaddr = 00:16:3e:47:bc:6d  

Pour arrêter **c1** :

    root@debian:~# lxc-stop c1

Nous allons maintenant cloner **c1** en **c2** puis en **c3** :

    root@debian:~# lxc-copy -n c1 -N c2
    root@debian:~# lxc-copy -n c1 -N c3
    root@debian:~# lxc-ls
    c1   c2   c3

Nous pouvons remarquer que le fichier de configuration de **c2** est similaire à celui de **c1**, seul l'adresse MAC a changé mais aussi l'ajout de deux lignes supplémentaires à la fin du fichier : 

    root@debian:~# cat /var/lib/lxc/c2/config 
    ...
    lxc.net.0.hwaddr = 00:16:3e:68:82:84
    lxc.rootfs.path = dir:/var/lib/lxc/c2/rootfs
    lxc.uts.name = c2
