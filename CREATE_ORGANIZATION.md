# Créer l'organisation @webify sur npm

## Étapes pour créer l'organisation

### 1. Aller sur npmjs.com
Ouvrez : https://www.npmjs.com/

### 2. Se connecter
Connectez-vous avec votre compte (ahmed-mili)

### 3. Créer l'organisation
1. Cliquez sur votre avatar (en haut à droite)
2. Allez dans **Settings**
3. Dans le menu de gauche, cliquez sur **Organizations**
4. Cliquez sur **Create Organization** ou **Add Organization**
5. Choisissez :
   - **Name**: `webify` (sans le @)
   - **Plan**: Free (pour les packages publics)
6. Confirmez la création

### 4. Vérifier depuis la ligne de commande

```bash
npm org ls webify
```

### 5. Publier le package

Une fois l'organisation créée :

```bash
npm publish --access public
```

## Alternative : Publier sans scope

Si vous ne pouvez pas créer l'organisation maintenant, publiez sans scope :

### Modifier package.json

Remplacez :
```json
"name": "@webify/capacitor-thunder-bg-service"
```

Par :
```json
"name": "capacitor-thunder-bg-service"
```

Puis publiez :
```bash
npm publish --access public
```

## Vérification

Après publication, vérifiez :
```bash
npm view @webify/capacitor-thunder-bg-service
# ou
npm view capacitor-thunder-bg-service
```

