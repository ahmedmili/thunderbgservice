# Guide de Contribution

Merci de votre intérêt pour contribuer à Thunder Background Service !

## 🚀 Démarrage Rapide

1. Fork le projet
2. Créez une branche (`git checkout -b feature/AmazingFeature`)
3. Committez vos changements (`git commit -m 'Add some AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrez une Pull Request

## 📝 Standards de Code

### TypeScript

- Utilisez `strict` mode
- Suivez les conventions ESLint
- Ajoutez des types explicites
- Documentez les fonctions publiques

### Tests

- Écrivez des tests pour les nouvelles fonctionnalités
- Maintenez une couverture > 70%
- Utilisez des noms de tests descriptifs

### Commits

- Utilisez des messages de commit clairs
- Format : `type(scope): description`
- Types : `feat`, `fix`, `docs`, `test`, `refactor`, `chore`

## 🧪 Tests

```bash
# Avant de soumettre une PR
npm test
npm run lint
npm run test:coverage
```

## 📚 Documentation

- Mettez à jour la documentation si nécessaire
- Ajoutez des exemples pour les nouvelles fonctionnalités
- Vérifiez que `npm run docs` fonctionne

## 🐛 Signaler un Bug

Utilisez le template d'issue et incluez :
- Description du problème
- Étapes pour reproduire
- Comportement attendu vs actuel
- Environnement (OS, version, etc.)

## ✨ Proposer une Feature

Utilisez le template d'issue et incluez :
- Description de la feature
- Cas d'usage
- Problème résolu
- Alternatives considérées

## 📄 Licence

En contribuant, vous acceptez que vos contributions soient sous la licence MIT.

