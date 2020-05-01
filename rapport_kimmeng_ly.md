# TD SAS : Rapport final

Tout d'abord, pour pouvoir faire les travaux pratiques demandés, nous avons besoin d'un environnement de travail sous linux.  
Nous avons donc crée une machine virtuelle à partir de VirtualBox.  

Ensuite, nous avons installé une Debian Minimal à partir du mini.iso fourni par notre encadrant sur cette machine virtuelle.  

Pendant l'installation, nous avons choisi un mot de passe administrateur **root** (ce mot de passe peut être changé à tout moment avec la commande **passwd**) et crée un utilisateur *lambda* (sans droits administrateur) avec son mot de passe.  

Une fois l'installation faite, le terminal de base du système récemment installé étant pas très pratique, il est préférable de se connecter à la nouvelle machine depuis notre propre terminal via **ssh**.  
Sans toucher au fichier de configuation de ssh de base, nous pouvons seulement nous connecter en tant que utilisateur *lambda*.

    ssh root@192.168.0.30  
    root@192.168.0.30's password:  
    Permission denied, please try again.

Par conséquent, pour pouvoir nous connecter en tant que *root* (avec les droits administrateur), nous devons ajouter la ligne suivante dans le fichier **/etc/ssh/sshd_config**.  
    
    PermitRootLogin yes

Cette ligne permet à *root* de se connecter en SSH ou à un utilisateur *lambda* en **su** (super-utilisateur). Ainsi, après avoir ajouter cette ligne, nous redémarrons le service **ssh** via la commande :  

    /etc/init.d/ssh restart  

Une fois que le service **ssh** redémarré, nous pouvons enfin nous connecter en tant que *root*.


    ssh root@192.168.0.30  
    root@192.168.0.30's password:   
    root@debian:~#  


**TP2** Partitions ?  
**TP3** SSH génération de clés


Nous allons maintenant procéder à la mise en place des conteneurs **lxc**. Tout d'abord, nous devons installer les paquets de **lxc** avec la commande (en tant que *root*) :  

    apt-get install lxc lxctl lxc-tests lxc-templates 

Ensuite, nous pouvons créer notre premier conteneur *c1* avec la commande :  

    lxc-create -n c1 -t download  

**-n** : pour spécifier le nom du conteneur  
**-t** : pour spécifier le template  

Nous précisons ensuite quel type de distribution nous voulons installer :

    Distribution: debian
    Release: buster
    Architecture: amd64  

Une fois que notre conteneur est installé, nous pouvons le voir avec **lxc-ls** qui permet de lister les conteneurs installés : 

    lxc-ls
    c1  

