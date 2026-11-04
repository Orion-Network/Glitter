# Glitter et les core shaders : pistes

Notes de recherche, pas encore implémentées. Elles décrivent ce que les core shaders de Minecraft (1.21.11) peuvent
apporter à Glitter, et comment le faire. Le prototype qui valide la technique de base est le module de pack
`shader_lab` de MewtRPG-content (voir [Ce qui existe déjà](#ce-qui-existe-deja)).

## Le constat

Glitter affiche ses particules avec des entités d'affichage (`item_display`, `text_display`) envoyées par paquets.
Tout ce qui change pendant la vie d'une particule part en paquet, à chaque tick :

- le mouvement (`Motion.apply` : un `EntityPositionPacket` par particule et par tick) ;
- la taille (un `EntityMetaDataPacket`, désormais un seul grâce à l'interpolation du client) ;
- une suite d'images ou une couleur qui change (il faudrait renvoyer l'objet à chaque étape).

Glimmer (le plugin Spigot dont Glitter s'inspire) fait pareil : son « émissif » règle juste la lumière de l'entité à
15, et ses animations de couleur et d'images sont des paquets. Le halo lumineux qu'on voit dans ses GIF vient d'un pack
de shaders côté client (Iris), pas du plugin.

Avec les core shaders, une particule peut **tout faire sur le GPU à partir d'un seul paquet d'apparition**. C'est là
que Glitter peut faire mieux que Glimmer.

## Ce qu'il faut savoir sur les shaders en 1.21.11

Vérifié dans le jar client 1.21.11 (`assets/minecraft/shaders/`) :

- Les core shaders se remplacent par un resource pack : `assets/minecraft/shaders/core/<nom>.vsh|.fsh`. Les uniformes
  sont des blocs (`#moj_import <minecraft:globals.glsl>`, `dynamictransforms.glsl`, `projection.glsl`, `fog.glsl`).
- `Globals` (dont `GameTime`) est attaché à **tous** les rendus (`RenderSystem.bindDefaultUniforms`) : n'importe quel
  shader peut l'importer, même ceux qui ne le font pas en vanilla.
- `GameTime` = âge du monde modulo 24000, divisé par 24000 (de 0 à 1). En ticks : `GameTime * 24000.0`. Le serveur
  connaît la même horloge : `Instance#getWorldAge()`.
- Le vertex shader peut lire la texture (`texelFetch(Sampler0, ...)`) : c'est ce qui rend possibles les pixels
  d'information.
- Le shader `particle` (vraies particules du jeu) ne reçoit que position, texture, couleur et lumière. Il ne connaît ni
  l'âge de la particule ni son type : inutilisable pour des animations qui démarrent à l'apparition.
- Les nouveaux shaders `animate_sprite*` animent les textures `.mcmeta` sur le GPU, mais en boucle et pour tout le monde
  en même temps.
- Rendus qui nous concernent :
  - `text_display` → `rendertype_text` (`rendertype_text_see_through` si `see_through`) ;
  - `item_display` → les shaders d'entité / d'objet (`entity`, `rendertype_item_entity_translucent_cull`), à vérifier
    au cas par cas.

## La technique de base : faire passer des données au shader

Un shader ne voit que ce que le jeu lui donne pour chaque sommet. Deux canaux suffisent.

### 1. La couleur (par particule)

La couleur d'un texte (`text_display`), ou la teinte d'un objet (couleurs de `custom_model_data` sur un modèle teinté),
arrive telle quelle dans l'attribut `Color` du vertex shader : 24 bits libres par particule.

- **R = 254** : marqueur « particule Glitter » (aucun texte ordinaire n'a exactement ce rouge).
- **G** : tick de départ, modulo 256 (`worldAge % 24000 % 256`). Le shader calcule l'âge :
  `mod(GameTime * 24000.0 - G, 256.0)`.
- **B** : paramètre libre (graine aléatoire, numéro de trajectoire, variante de couleur...).

Une fois la donnée lue, le shader remet `vertexColor` à blanc : la couleur est une donnée, pas une teinte.

### 2. Les pixels d'information (par type de particule)

Technique de JNNGL (`animated_unicodes`) et de fancyPants : des pixels presque transparents (alpha 1) rangés dans la
texture elle-même. Ils ne s'affichent jamais (le fragment shader jette tout ce qui est sous 0.1) mais le vertex shader
peut les lire.

Format utilisé par `shader_lab` (bordure d'un pixel autour de la grille d'images) :

| Pixel | Contenu |
|---|---|
| (0, 0) | nombre magique 77, 69, 87 (« MEW ») : texture Glitter |
| (1, 0) | largeur d'une image, hauteur d'une image, nombre d'images |
| (2, 0) | colonnes de la grille, ticks par image |
| (L-1, 0), (0, H-1), (L-1, H-1) | recul jusqu'à (0, 0) : `R + 256 * (B & 15)` en x, `G + 256 * (B >> 4)` en y |

Un sommet ne connaît que son propre coin : les coins pointent vers l'origine, où se lisent les données. Les pixels
libres de la ligne 0 peuvent porter d'autres paramètres (trajectoire, vitesse, couleurs de dégradé...).

### Limites

- `GameTime` repart de 0 toutes les 24000 ticks (20 min) et 24000 n'est pas un multiple de 256 : un effet lancé juste
  avant ce passage saute. Si ça se voit, envoyer au client un compteur propre (par la lumière, voir plus bas).
- 256 ticks au plus (12,8 s) entre l'apparition et la fin d'une animation, à cause du modulo 256 de G.
- Les particules restent des entités : le serveur doit toujours les retirer (une seule fois, à la fin).
- Un shader de pack remplace celui des autres packs du client sur le même fichier : un seul `rendertype_text.vsh` doit
  réunir tous nos effets.

## Les idées, par ordre d'intérêt

### 1. Mouvement sur le GPU (le plus gros gain)

**But** : supprimer les paquets de position. Une particule apparaît, le GPU la fait bouger.

**Comment** :

- Côté texture, un pixel d'information choisit une **trajectoire prédéfinie** et ses réglages :
  - `RISE` : montée, avec un balancement (fumée, étincelles) ;
  - `BURST` : départ vers l'extérieur, qui ralentit (explosion) ;
  - `SPIRAL` : montée en spirale (soin, invocation) ;
  - `FALL` : chute avec gravité (débris, pluie).
- Côté couleur, **B = graine** : le shader en tire une direction et une vitesse pseudo-aléatoires
  (`hash(seed)`), pour que 30 particules d'un même émetteur partent dans 30 directions différentes avec un seul type
  de texture.
- Dans le vertex shader, après avoir trouvé l'origine des données :

  ```glsl
  float age = mod(GameTime * 24000.0 - float(data.g), 256.0) / 20.0;   // secondes
  vec3 dir = normalize(hash3(float(data.b)) * 2.0 - 1.0);
  vec3 offset = dir * speed * (1.0 - exp(-drag * age));               // BURST
  // RISE : offset = vec3(sin(age * 3.0 + seed) * 0.2, age * speed, 0.0);
  gl_Position = ProjMat * ModelViewMat * vec4(Position + offset, 1.0);
  ```

  Attention : pour un `text_display` en `billboard: center`, `Position` est déjà dans l'espace de la caméra une fois le
  quad tourné. Il faut appliquer le décalage en espace monde : `ModelViewMat` contient la rotation de la caméra, donc
  `offset` doit passer par l'inverse de sa partie rotation, ou bien rester en espace vue (plus simple : une montée en
  espace vue reste « vers le haut de l'écran », ce qui suffit pour beaucoup d'effets).
- Côté Glitter : un nouveau `Motion` (par exemple `GpuMotion(Trajectory, speed, drag)`) qui ne renvoie **aucun**
  paquet dans `apply`. `Emitter.emit` donne à chaque particule une couleur `(254, start, seed)`.

**Limites** : pas de collision avec les blocs (le serveur ne sait plus où est la particule), trajectoires figées dans
le shader (on ne peut pas y traduire les `FormulaMotion` arbitraires de Glitter).

### 2. Suite d'images sur le GPU (déjà prototypée)

**But** : animer une texture image par image, à partir de l'apparition, sans changer de modèle à chaque image (ce que
fait Glimmer avec `custom_model_data`).

**Comment** : c'est exactement `shader_lab` (voir plus bas). À porter dans Glitter :

- une `TextAppearance` dont le texte est le caractère de l'effet, dans la police `orion:fx` ;
- la couleur `(254, start, 0)` posée par `Emitter.emit` au moment de l'apparition ;
- la durée de vie de la particule = `images × ticks par image`.

### 3. Dégradé de couleur et fondu de fin de vie

**But** : une particule qui passe du jaune au rouge puis s'efface, sans paquet.

**Comment** :

- Pixels d'information : couleur de départ (3, 0), couleur de fin (4, 0), durée et forme de la courbe (5, 0).
- Dans le vertex shader : `vertexColor = vec4(mix(start, end, t), 1.0 - smoothstep(fadeStart, 1.0, t))` avec
  `t = age / durée`. Le fragment shader multiplie déjà la texture par `vertexColor`.
- Bonus : les objets affichés (`item_display`) n'ont pas de transparence réglable ; le shader la leur donne.

### 4. Pixels émissifs dans n'importe quelle texture

**But** : armes, minerais, yeux de boss qui brillent dans le noir, sans entité d'affichage.

**Comment** (VanillaDynamicEmissives) : marquer les pixels lumineux d'une texture avec **alpha 252**. Dans le fragment
shader concerné (`entity.fsh`, `terrain.fsh`, `rendertype_item_entity_translucent_cull.fsh`...) :

```glsl
vec4 tex = texture(Sampler0, texCoord0);
if (int(round(tex.a * 255.0)) == 252) {
    fragColor = vec4(tex.rgb, 1.0);   // ignore la lumière du lieu
    return;
}
```

Le vertex shader doit transmettre au fragment shader la couleur **sans** la lumière pour que ce soit possible
(aujourd'hui `vertexColor = Color * lumière`).

### 5. Faux halo (bloom approché)

**But** : un halo visible par tous, sans pack de shaders côté client.

Le vrai bloom demande un post-traitement : en vanilla, seulement en graphismes « Fabulous » (le Bloom de JNNGL en
dépend). Approximation à la portée des core shaders :

- dessiner le halo **dans la texture** : un cœur très lumineux entouré de pixels semi-transparents de la même teinte
  (le plus simple, et ce qui rend le mieux) ;
- ou une deuxième particule plus grande, transparente et émissive, au même endroit (deux entités, mais aucun paquet
  par tick si le reste est sur le GPU).

### 6. Données globales envoyées par le serveur

**But** : piloter un effet plein écran ou un paramètre global depuis le serveur (flash à l'impact d'une attaque de
boss, horloge propre au lieu de `GameTime`).

**Comment** : JNNGL (`Lightmap Uniforms`, `Dynamic FOV`) fait passer des valeurs par la texture de lumière ou par les
paquets de temps. À creuser quand on en aura besoin.

## Ce qui existe déjà

Dans MewtRPG et MewtRPG-content (prototype `shader_lab`) :

- `packs/shader_lab/assets/minecraft/shaders/core/rendertype_text.vsh` : lecture des pixels d'information, choix de
  l'image selon `GameTime` et la couleur, effet joué une fois ou en boucle, rendu sans lumière ni teinte ;
- `tools/fx.py` : génère les grilles d'images avec leurs pixels d'information, la police `orion:fx`, la table
  `effects.json`, un zip et un datapack pour tester en solo ;
- `fr.dandelion.vfx.ShaderFx` : fait apparaître un effet (couleur de départ calculée depuis `getWorldAge`) et le retire
  à la fin ;
- `/test fx <effet|all> [boucle] [taille]` pour essayer en jeu.

## Plan proposé

1. Valider `shader_lab` en jeu (effet joué une fois, aligné sur l'horloge du serveur).
2. Ajouter la trajectoire `BURST` au shader, avec la graine dans B, et un `/test fx burst` qui en lance 30.
3. Porter dans Glitter : `GpuMotion`, couleur posée à l'émission, `TextAppearance` sur la police d'effets.
4. Puis dégradé et fondu, puis pixels émissifs selon les besoins.

## Références

- JNNGL, vanilla-shaders (`animated_unicodes`, `Lightmap Uniforms`, `Bloom`) : https://github.com/JNNGL/vanilla-shaders
- Ancientkingg, fancyPants (pixels d'information dans les textures d'armure) : https://github.com/Ancientkingg/fancyPants
- ShockMicro, VanillaDynamicEmissives (alpha 252) : https://github.com/ShockMicro/VanillaDynamicEmissives
- McTsts, mc-core-shaders (utilitaires, marqueurs d'opacité 254) : https://github.com/McTsts/mc-core-shaders
- Ethermoor, Glimmer (plugin de particules dont Glitter s'inspire) : https://github.com/Ethermoor/GlimmerWiki/wiki
