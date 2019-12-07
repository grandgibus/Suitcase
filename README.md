# Parcours de déportés et résistants
![Suitcase opened](https://i.imgur.com/7Q7FTZM.jpg)

Projet originellement conçu pour le Concours National de la Résistance et Déportation.
Le projet est une valise qui une fois ouverte possède sur la partie supérieure un écran capable d'afficher des vidéos relatant l'histoire de différents déportés et résistants durant l'occupation nazie pendant la seconde guerre mondiale. La partie inférieure de la valise possède une carte en relief d’Europe avec des diodes sur les lieux mentionnés dans les parcours, ainsi qu'une frise chronologique avec les années des dates clés. 
Elle possède aussi des enceintes qui permet d'entendre les vidéos-documentaires, ainsi qu'un bouton lumineux pour contrôler le lancement des parcours et deux autres petits boutons poussoirs pour contrôler le volume sonore.

![Suitcase inferior part](https://i.imgur.com/cAoCVII.jpg)

## Conception structurelle
La quasi intégralité de la modélisation CAD a été réalisée sur OnShape, ce qui s'est finalement avéré être une erreur de par la complexité de l'assemblage à manipuler, comportant trop de pièces pour être efficacement développé.

Le projet peut être vu sur: https://cad.onshape.com/documents/2e7daea08c12ab10e9ac8ab1/w/2863503051dc8fdf42f90177/e/47789e8cf90c5e76813362f4

![drawing](https://i.imgur.com/jm6Ic2s.png)

![drawing](https://i.imgur.com/etDDnJC.png)

La première étape a été la modélisation de la valise, qui est une vieille valise en carton des années 1960. Il a ensuite fallu modéliser l’intégralité des autres composants:

 - Ecran IPS récupéré d'un ancien pc portable
 - Carte driver de l'écran
 - Perfboard
 - Buck converter
 - Mono class D amplifier
 - Haut parleurs 4Ohms midrange
 - Noctua fan
 - Divers (Jack plug male/female, pushbutton, rotary encoder, leds, etc..)

La deuxième étape a été de modéliser un cadre sur la partie supérieure et inférieure, divisé en 6 portions pour pouvoir être imprimées sur une Prusa i3 MK3 en PETG. Le cadre est fixé à l'aide de zipties reliées à des crochets qui sont eux-mêmes rivetés à la valise. Les poinçons sur les bords du cadre permettent de faire pression sur les bords de la valise afin de stabiliser le cadre.

Tous les composants du circuit sont fixés sur le cadre, et finalement des caches sont ajoutés pour masquer l'intérieur de la valise. Le cache supérieur est imprimé en 6 pièces, et le cache inférieur a été fraisé sur une fraiseuse d'un ami.

La carte et la frise chronologique sont montés sur des pilliers tenants sur le cadre.

![bottom view](https://i.imgur.com/yLeekrQ.png)

Redesign du module boutons sur Fusion 360:
https://a360.co/2OINKDt

## Conception circuit / programme
Le projet est basé sur une Raspberry Pi 3B.
Le programme est lancé en tant que service debian lors du boot de la carte.
L'integralité du logiciel est programmé en Java avec pour framework GUI Swing. La lecture de vidéo est effectuée avec VLC grâce à la lib [vlcj](https://github.com/caprica/vlcj) de caprica.
Le contrôle du GPIO est fait grâce [pi4j](https://pi4j.com/1.2/index.html)

La programmation du déclenchement des leds sur la carte et la frise est interprétée grâce a des fichiers de configurations notés en json.
Les fichiers jsons sont générés automatiquement grâce à une application simple développée sous electron qui permet de rapidement configurer les moments où les leds s'allument et s'éteignent.

![led programming tool](https://i.imgur.com/Yf1hVsy.png)

Le fonctionnement général du circuit est le suivant:

![circuit diagram](https://i.imgur.com/lVeUvzt.png)

Les leds sont dirigées par des TLC5940 de Texas Instruments. Trois ICs sont positionnés en daisy-chain et dirigent l'intégralité des diodes présentent. Les TLC5940 supportent le grayscale pour les leds, mais à cause de problèmes de performance du processeur de la raspberry pi et du code écrit en java, qui est plus éloigné du processeur que le C++, cette fonctionnalité n'est pas utilisée.
L'interface se présente de cette manière une fois le système démarré:

![menu](https://i.imgur.com/ZJy6SyE.png)

Le point représente la sélection, un appui court change la sélection et un appui long les démarre.
Tous les graphismes et montages sont réalisés avec la suite Adobe.

Malgré de très nombreux problèmes rencontrés, la partie technique de la valise étant mon premier réel projet aussi développé, il fonctionne (avec un retard de seulement une année). Il y a énormément d'autres choses à dire mais qui ne tiennent pas dans un résumé.

Merci à la classe et leur travail sur la recherche et la réalisation des différents parcours historiques, à Mme DERUYTER et Mme SOGLIUZZO, et à internet qui permet d'apprendre l'intégralité des compétences requises à la réalisation de ce projet gratuitement

<sub>
Projet réalisé par la classe 1F - 2018/2019 au Lycée Beaumont
<br>
<br>
Parcours d’Huguette GALLAIS:<br>
Salomé DAVID, Manon LAIGLE, Louis GICQUEL, Yann TIGER
<br>
<br>
Parcours de Lili LEIGNEL:<br>
Mélanie JOUBAUD, Alicia BORDE, Léa HERVÉ, Maël DEBACK
<br>
<br>
Parcours de Raymond GURÊME:<br>
Nathan JOUNIER, Hortence MARTIN, Margot GUEDON,
Clément GUIGAND, Océane PEDRON
<br>
<br>
Parcours de Marie-Claude VAILLANT-COUTURIER:<br>
Lucie MONGAULT, Ophélie DELMAS
<br>
<br>
Réalisation technique:
Rémi GRANDZINSKI
</sub>
