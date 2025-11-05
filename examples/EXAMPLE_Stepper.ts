/**
 * EXEMPLE: Notification avec Stepper (pages multiples avec boutons)
 * 
 * Cet exemple montre comment créer une notification avec plusieurs pages
 * (stepper) et des boutons pour naviguer entre les étapes.
 * 
 * IMPORTANT: 
 * - Le plugin n'a pas de UI par défaut, customLayout est REQUIS
 * - Utilisez viewData pour injecter dynamiquement les textes
 * - Utilisez buttons pour rendre les boutons cliquables
 * - L'état est automatiquement persisté (fermeture/ouverture de l'app)
 */

import { ThunderBgService } from '@ahmed-mili/capacitor-thunder-bg-service';

// États du stepper (chaque état = une page)
enum StepperStep {
  STEP_1 = 'STEP_1',
  STEP_2 = 'STEP_2',
  STEP_3 = 'STEP_3',
  STEP_4 = 'STEP_4',
}

/**
 * Exemple 1: Démarrer avec un stepper (page 1)
 */
export async function startStepper() {
  await ThunderBgService.start({
    customLayout: 'notification_stepper', // REQUIS: Layout avec stepper
    titleViewId: 'txtStepperTitle',
    subtitleViewId: 'txtStepperSubtitle',
    timerViewId: 'txtStepperTimer',
    enableLocation: true,
    viewData: {
      txtStepperTitle: 'Étape 1/4',
      txtStepperSubtitle: 'Bienvenue dans le processus',
      txtStepperStep: 'Étape 1',
      txtStepperDescription: 'Description de l\'étape 1',
      txtStepperTimer: '00:00:00',
    },
    buttons: [
      { viewId: 'btnStepperNext', action: 'com.yourapp.ACTION_STEPPER_NEXT' },
      { viewId: 'btnStepperReset', action: 'com.yourapp.ACTION_STEPPER_RESET' },
    ],
  });
}

/**
 * Exemple 2: Naviguer vers l'étape suivante
 */
export async function goToNextStep(currentStep: StepperStep): Promise<StepperStep | null> {
  let nextStep: StepperStep | null = null;
  let viewData: { [key: string]: string } = {};
  
  switch (currentStep) {
    case StepperStep.STEP_1:
      nextStep = StepperStep.STEP_2;
      viewData = {
        txtStepperTitle: 'Étape 2/4',
        txtStepperSubtitle: 'Continuez le processus',
        txtStepperStep: 'Étape 2',
        txtStepperDescription: 'Description de l\'étape 2',
      };
      break;
    case StepperStep.STEP_2:
      nextStep = StepperStep.STEP_3;
      viewData = {
        txtStepperTitle: 'Étape 3/4',
        txtStepperSubtitle: 'Presque terminé',
        txtStepperStep: 'Étape 3',
        txtStepperDescription: 'Description de l\'étape 3',
      };
      break;
    case StepperStep.STEP_3:
      nextStep = StepperStep.STEP_4;
      viewData = {
        txtStepperTitle: 'Étape 4/4',
        txtStepperSubtitle: 'Dernière étape',
        txtStepperStep: 'Étape 4',
        txtStepperDescription: 'Description de l\'étape 4',
      };
      // Dernière étape: changer les boutons
      await ThunderBgService.update({
        viewData,
        buttons: [
          { viewId: 'btnStepperPrev', action: 'com.yourapp.ACTION_STEPPER_PREV' },
          { viewId: 'btnStepperComplete', action: 'com.yourapp.ACTION_STEPPER_COMPLETE' },
        ],
      });
      return nextStep;
    case StepperStep.STEP_4:
      // Déjà à la dernière étape
      return null;
  }
  
  // Mettre à jour avec les boutons appropriés
  await ThunderBgService.update({
    viewData,
    buttons: [
      { viewId: 'btnStepperPrev', action: 'com.yourapp.ACTION_STEPPER_PREV' },
      { viewId: 'btnStepperNext', action: 'com.yourapp.ACTION_STEPPER_NEXT' },
      { viewId: 'btnStepperReset', action: 'com.yourapp.ACTION_STEPPER_RESET' },
    ],
  });
  
  return nextStep;
}

/**
 * Exemple 3: Naviguer vers l'étape précédente
 */
export async function goToPrevStep(currentStep: StepperStep): Promise<StepperStep | null> {
  let prevStep: StepperStep | null = null;
  let viewData: { [key: string]: string } = {};
  
  switch (currentStep) {
    case StepperStep.STEP_4:
      prevStep = StepperStep.STEP_3;
      viewData = {
        txtStepperTitle: 'Étape 3/4',
        txtStepperSubtitle: 'Presque terminé',
        txtStepperStep: 'Étape 3',
        txtStepperDescription: 'Description de l\'étape 3',
      };
      break;
    case StepperStep.STEP_3:
      prevStep = StepperStep.STEP_2;
      viewData = {
        txtStepperTitle: 'Étape 2/4',
        txtStepperSubtitle: 'Continuez le processus',
        txtStepperStep: 'Étape 2',
        txtStepperDescription: 'Description de l\'étape 2',
      };
      break;
    case StepperStep.STEP_2:
      prevStep = StepperStep.STEP_1;
      viewData = {
        txtStepperTitle: 'Étape 1/4',
        txtStepperSubtitle: 'Bienvenue dans le processus',
        txtStepperStep: 'Étape 1',
        txtStepperDescription: 'Description de l\'étape 1',
      };
      // Première étape: pas de bouton "Précédent"
      await ThunderBgService.update({
        viewData,
        buttons: [
          { viewId: 'btnStepperNext', action: 'com.yourapp.ACTION_STEPPER_NEXT' },
          { viewId: 'btnStepperReset', action: 'com.yourapp.ACTION_STEPPER_RESET' },
        ],
      });
      return prevStep;
    case StepperStep.STEP_1:
      // Déjà à la première étape
      return null;
  }
  
  // Mettre à jour avec les boutons appropriés
  await ThunderBgService.update({
    viewData,
    buttons: [
      { viewId: 'btnStepperPrev', action: 'com.yourapp.ACTION_STEPPER_PREV' },
      { viewId: 'btnStepperNext', action: 'com.yourapp.ACTION_STEPPER_NEXT' },
      { viewId: 'btnStepperReset', action: 'com.yourapp.ACTION_STEPPER_RESET' },
    ],
  });
  
  return prevStep;
}

/**
 * Exemple 4: Réinitialiser le stepper
 */
export async function resetStepper() {
  await ThunderBgService.update({
    viewData: {
      txtStepperTitle: 'Étape 1/4',
      txtStepperSubtitle: 'Bienvenue dans le processus',
      txtStepperStep: 'Étape 1',
      txtStepperDescription: 'Description de l\'étape 1',
      txtStepperTimer: '00:00:00',
    },
    buttons: [
      { viewId: 'btnStepperNext', action: 'com.yourapp.ACTION_STEPPER_NEXT' },
      { viewId: 'btnStepperReset', action: 'com.yourapp.ACTION_STEPPER_RESET' },
    ],
  });
}

/**
 * Exemple 5: Gestionnaire complet de stepper
 */
export class StepperManager {
  private currentStep: StepperStep = StepperStep.STEP_1;

  async initialize() {
    await startStepper();
    this.currentStep = StepperStep.STEP_1;
  }

  async next() {
    const next = await goToNextStep(this.currentStep);
    if (next) {
      this.currentStep = next;
    }
  }

  async prev() {
    const prev = await goToPrevStep(this.currentStep);
    if (prev) {
      this.currentStep = prev;
    }
  }

  async reset() {
    await resetStepper();
    this.currentStep = StepperStep.STEP_1;
  }

  async complete() {
    // Terminer le processus
    await ThunderBgService.update({
      viewData: {
        txtStepperTitle: 'Terminé !',
        txtStepperSubtitle: 'Processus complété avec succès',
        txtStepperStep: 'Terminé',
        txtStepperDescription: 'Toutes les étapes sont terminées.',
      },
      buttons: [
        { viewId: 'btnStepperReset', action: 'com.yourapp.ACTION_STEPPER_RESET' },
        { viewId: 'btnStepperClose', action: 'com.yourapp.ACTION_STEPPER_CLOSE' },
      ],
    });
    
    // Arrêter le service après 5 secondes
    setTimeout(async () => {
      await ThunderBgService.stop();
    }, 5000);
  }

  getCurrentStep(): StepperStep {
    return this.currentStep;
  }
}

/**
 * Exemple 6: Utilisation dans un service Angular/React
 */
export class MyStepperService {
  private stepperManager = new StepperManager();

  async onInit() {
    await this.stepperManager.initialize();
  }

  // Appelé depuis le BroadcastReceiver Java quand btnStepperNext est cliqué
  async onStepperNextClicked() {
    await this.stepperManager.next();
  }

  // Appelé depuis le BroadcastReceiver Java quand btnStepperPrev est cliqué
  async onStepperPrevClicked() {
    await this.stepperManager.prev();
  }

  // Appelé depuis le BroadcastReceiver Java quand btnStepperReset est cliqué
  async onStepperResetClicked() {
    await this.stepperManager.reset();
  }

  // Appelé depuis le BroadcastReceiver Java quand btnStepperComplete est cliqué
  async onStepperCompleteClicked() {
    await this.stepperManager.complete();
  }
}

/**
 * NOTES IMPORTANTES:
 * 
 * 1. Layout XML requis (res/layout/notification_stepper.xml):
 *    - txtStepperTitle (TextView)
 *    - txtStepperSubtitle (TextView)
 *    - txtStepperStep (TextView)
 *    - txtStepperDescription (TextView)
 *    - txtStepperTimer (TextView)
 *    - btnStepperNext (Button)
 *    - btnStepperPrev (Button)
 *    - btnStepperReset (Button)
 *    - btnStepperComplete (Button)
 *    - btnStepperClose (Button)
 * 
 * 2. BroadcastReceiver requis dans votre app (ex: NotifActionReceiver.java):
 *    - ACTION_STEPPER_NEXT
 *    - ACTION_STEPPER_PREV
 *    - ACTION_STEPPER_RESET
 *    - ACTION_STEPPER_COMPLETE
 *    - ACTION_STEPPER_CLOSE
 * 
 * 3. Le plugin persiste automatiquement l'état:
 *    - Si vous fermez/rouvrez l'app, le stepper reste à la même étape
 *    - Les textes et boutons sont restaurés automatiquement
 * 
 * 4. Le timer (txtStepperTimer) est mis à jour automatiquement par le heartbeat
 *    si vous avez fourni timerViewId lors du start()
 */

