# Dépannage de la publication npm

## Erreur 404 : "Not Found" ou "do not have permission"

Cette erreur signifie généralement que :
1. L'organisation `@webify` n'existe pas sur npm
2. Vous n'êtes pas membre de l'organisation
3. Vous n'avez pas les permissions de publication

## Solutions

### Solution 1 : Créer l'organisation `@webify` sur npm

1. Allez sur https://www.npmjs.com/
2. Connectez-vous avec votre compte
3. Allez dans **Settings** → **Organizations**
4. Cliquez sur **Create Organization**
5. Choisissez le nom : `webify`
6. Choisissez le plan (gratuit pour les packages publics)

### Solution 2 : Vérifier votre appartenance à l'organisation

```bash
# Vérifier les organisations dont vous êtes membre
npm org ls webify
```

Si vous n'êtes pas membre, demandez à l'administrateur de vous ajouter.

### Solution 3 : Publier sans scope (temporairement)

Si vous voulez tester la publication sans créer l'organisation :

1. Modifiez `package.json` :
```json
{
  "name": "capacitor-thunder-bg-service",
  ...
}
```

2. Publiez :
```bash
npm publish --access public
```

3. Puis republiez sous `@webify` une fois l'organisation créée.

### Solution 4 : Vérifier votre connexion npm

```bash
# Vérifier qui vous êtes connecté
npm whoami

# Si ce n'est pas le bon compte, reconnectez-vous
npm logout
npm login
```

### Solution 5 : Vérifier les permissions

Si l'organisation existe mais vous n'avez pas les permissions :

1. Allez sur https://www.npmjs.com/org/webify
2. Vérifiez votre rôle (Admin, Developer, etc.)
3. Contactez l'administrateur pour obtenir les permissions de publication

## Étapes recommandées

### Étape 1 : Créer l'organisation

1. Connectez-vous sur npmjs.com
2. Créez l'organisation `webify`
3. Choisissez le plan gratuit (pour les packages publics)

### Étape 2 : Vérifier la connexion

```bash
npm whoami
# Doit afficher votre username npm
```

### Étape 3 : Publier

```bash
npm publish --access public
```

## Alternative : Publier sans organisation

Si vous ne pouvez pas créer l'organisation maintenant, vous pouvez publier sans scope :

### Modifier package.json

```json
{
  "name": "capacitor-thunder-bg-service",
  ...
}
```

**Note** : Changez aussi les imports dans votre code si vous utilisez ce nom.

### Avantages
- Publication immédiate
- Pas besoin d'organisation
- Test rapide

### Inconvénients
- Pas de namespace (risque de conflit de nom)
- Moins professionnel

## Vérification après publication

```bash
# Vérifier que le package est publié
npm view @webify/capacitor-thunder-bg-service

# Ou sans scope
npm view capacitor-thunder-bg-service
```

## Besoin d'aide ?

- [npm Documentation](https://docs.npmjs.com/)
- [npm Support](https://www.npmjs.com/support)

