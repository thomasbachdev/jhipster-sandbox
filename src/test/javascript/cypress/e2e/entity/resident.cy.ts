import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('Resident e2e test', () => {
  const residentPageUrl = '/resident';
  const residentPageUrlPattern = new RegExp('/resident(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const residentSample = {
    numero: 28187,
    nom: 'Orqsm',
    prenom: 'Ejks',
    dateNaissance: '2022-11-19',
    sexe: 'NON_BINAIRE',
    dateArrivee: '2022-11-19',
    chambre: 'back-e',
    taille: 11489,
  };

  let resident;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/residents+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/residents').as('postEntityRequest');
    cy.intercept('DELETE', '/api/residents/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (resident) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/residents/${resident.id}`,
      }).then(() => {
        resident = undefined;
      });
    }
  });

  it('Residents menu should load Residents page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('resident');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Resident').should('exist');
    cy.url().should('match', residentPageUrlPattern);
  });

  describe('Resident page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(residentPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Resident page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/resident/new$'));
        cy.getEntityCreateUpdateHeading('Resident');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', residentPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/residents',
          body: residentSample,
        }).then(({ body }) => {
          resident = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/residents+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [resident],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(residentPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Resident page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('resident');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', residentPageUrlPattern);
      });

      it('edit button click should load edit Resident page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Resident');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', residentPageUrlPattern);
      });

      it.skip('edit button click should load edit Resident page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Resident');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', residentPageUrlPattern);
      });

      it('last delete button click should delete instance of Resident', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('resident').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', residentPageUrlPattern);

        resident = undefined;
      });
    });
  });

  describe('new Resident page', () => {
    beforeEach(() => {
      cy.visit(`${residentPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Resident');
    });

    it('should create an instance of Resident', () => {
      cy.get(`[data-cy="numero"]`).type('68824').should('have.value', '68824');

      cy.get(`[data-cy="nom"]`).type('Ymqgh').should('have.value', 'Ymqgh');

      cy.get(`[data-cy="prenom"]`).type('Mp').should('have.value', 'Mp');

      cy.get(`[data-cy="dateNaissance"]`).type('2022-11-20').blur().should('have.value', '2022-11-20');

      cy.get(`[data-cy="sexe"]`).select('MASCULIN');

      cy.get(`[data-cy="dateArrivee"]`).type('2022-11-19').blur().should('have.value', '2022-11-19');

      cy.get(`[data-cy="chambre"]`).type('silver').should('have.value', 'silver');

      cy.get(`[data-cy="taille"]`).type('41003').should('have.value', '41003');

      cy.get(`[data-cy="denutrition"]`).select('MODEREE');

      cy.get(`[data-cy="deleted"]`).should('not.be.checked');
      cy.get(`[data-cy="deleted"]`).click().should('be.checked');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        resident = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', residentPageUrlPattern);
    });
  });
});
