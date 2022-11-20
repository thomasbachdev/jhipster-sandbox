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

describe('Docteur e2e test', () => {
  const docteurPageUrl = '/docteur';
  const docteurPageUrlPattern = new RegExp('/docteur(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const docteurSample = {};

  let docteur;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/docteurs+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/docteurs').as('postEntityRequest');
    cy.intercept('DELETE', '/api/docteurs/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (docteur) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/docteurs/${docteur.id}`,
      }).then(() => {
        docteur = undefined;
      });
    }
  });

  it('Docteurs menu should load Docteurs page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('docteur');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Docteur').should('exist');
    cy.url().should('match', docteurPageUrlPattern);
  });

  describe('Docteur page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(docteurPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Docteur page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/docteur/new$'));
        cy.getEntityCreateUpdateHeading('Docteur');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', docteurPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/docteurs',
          body: docteurSample,
        }).then(({ body }) => {
          docteur = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/docteurs+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [docteur],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(docteurPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Docteur page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('docteur');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', docteurPageUrlPattern);
      });

      it('edit button click should load edit Docteur page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Docteur');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', docteurPageUrlPattern);
      });

      it.skip('edit button click should load edit Docteur page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Docteur');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', docteurPageUrlPattern);
      });

      it('last delete button click should delete instance of Docteur', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('docteur').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', docteurPageUrlPattern);

        docteur = undefined;
      });
    });
  });

  describe('new Docteur page', () => {
    beforeEach(() => {
      cy.visit(`${docteurPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Docteur');
    });

    it('should create an instance of Docteur', () => {
      cy.get(`[data-cy="deleted"]`).should('not.be.checked');
      cy.get(`[data-cy="deleted"]`).click().should('be.checked');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        docteur = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', docteurPageUrlPattern);
    });
  });
});
