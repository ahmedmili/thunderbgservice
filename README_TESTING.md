# Guide de Tests - Thunder Background Service

Ce guide explique comment exécuter et écrire des tests pour le plugin Thunder Background Service.

## 📋 Prérequis

```bash
npm install
```

## 🧪 Exécuter les Tests

### Tests unitaires
```bash
npm test
```

### Tests en mode watch
```bash
npm run test:watch
```

### Tests avec couverture
```bash
npm run test:coverage
```

## 📊 Couverture de Code

Le projet vise une couverture minimale de **70%** pour :
- Branches
- Functions
- Lines
- Statements

Voir le rapport dans `coverage/index.html` après avoir exécuté `npm run test:coverage`.

## 🎯 Structure des Tests

```
__tests__/
├── setup.ts              # Configuration globale
├── definitions.test.ts   # Tests des types TypeScript
├── index.test.ts         # Tests du plugin principal
└── helpers.test.ts       # Tests des helpers
```

## ✍️ Écrire de Nouveaux Tests

### Exemple de test unitaire

```typescript
describe('MyFeature', () => {
  beforeEach(() => {
    // Setup avant chaque test
  });

  it('should do something', () => {
    // Arrange
    const input = 'test';
    
    // Act
    const result = myFunction(input);
    
    // Assert
    expect(result).toBe('expected');
  });
});
```

### Exemple de test d'intégration

```typescript
describe('ThunderBgService Integration', () => {
  it('should start service successfully', async () => {
    const result = await ThunderBgService.start({
      notificationTitle: 'Test',
    });
    
    expect(result.started).toBe(true);
  });
});
```

## 🔍 Linting

### Vérifier le code
```bash
npm run lint
```

### Corriger automatiquement
```bash
npm run lint:fix
```

## 📝 Documentation API

### Générer la documentation
```bash
npm run docs
```

La documentation sera générée dans `docs/api/`.

### Mode watch
```bash
npm run docs:watch
```

## 🚀 CI/CD

Les tests sont exécutés automatiquement via GitHub Actions sur :
- Push vers `main` ou `develop`
- Pull requests vers `main` ou `develop`

Le pipeline inclut :
- ✅ Tests unitaires
- ✅ Linting
- ✅ Build Android
- ✅ Vérification iOS
- ✅ Scan de sécurité
- ✅ Génération de documentation

## 🐛 Debugging

### Mode debug Jest
```bash
node --inspect-brk node_modules/.bin/jest --runInBand
```

Puis connectez Chrome DevTools à `chrome://inspect`.

## 📚 Ressources

- [Jest Documentation](https://jestjs.io/docs/getting-started)
- [TypeScript Testing](https://jestjs.io/docs/getting-started#using-typescript)
- [ESLint Rules](https://eslint.org/docs/rules/)

