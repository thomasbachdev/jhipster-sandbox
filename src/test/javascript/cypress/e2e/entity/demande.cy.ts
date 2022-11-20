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

describe('Demande e2e test', () => {
  const demandePageUrl = '/demande';
  const demandePageUrlPattern = new RegExp('/demande(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const demandeSample = { dateEmition: '2022-11-19T17:03:36.925Z', dateLimite: '2022-11-20', description: 'a' };

  let demande;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/demandes+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/demandes').as('postEntityRequest');
    cy.intercept('DELETE', '/api/demandes/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (demande) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/demandes/${demande.id}`,
      }).then(() => {
        demande = undefined;
      });
    }
  });

  it('Demandes menu should load Demandes page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('demande');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Demande').should('exist');
    cy.url().should('match', demandePageUrlPattern);
  });

  describe('Demande page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(demandePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Demande page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/demande/new$'));
        cy.getEntityCreateUpdateHeading('Demande');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', demandePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/demandes',
          body: demandeSample,
        }).then(({ body }) => {
          demande = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/demandes+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [demande],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(demandePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Demande page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('demande');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', demandePageUrlPattern);
      });

      it('edit button click should load edit Demande page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Demande');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', demandePageUrlPattern);
      });

      it.skip('edit button click should load edit Demande page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Demande');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', demandePageUrlPattern);
      });

      it('last delete button click should delete instance of Demande', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('demande').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', demandePageUrlPattern);

        demande = undefined;
      });
    });
  });

  describe('new Demande page', () => {
    beforeEach(() => {
      cy.visit(`${demandePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Demande');
    });

    it('should create an instance of Demande', () => {
      cy.get(`[data-cy="dateEmition"]`).type('2022-11-20T05:24').blur().should('have.value', '2022-11-20T05:24');

      cy.get(`[data-cy="dateLimite"]`).type('2022-11-20').blur().should('have.value', '2022-11-20');

      cy.get(`[data-cy="description"]`).type('invoice Optional Car').should('have.value', 'invoice Optional Car');

      cy.get(`[data-cy="deleted"]`).should('not.be.checked');
      cy.get(`[data-cy="deleted"]`).click().should('be.checked');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        demande = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', demandePageUrlPattern);
    });
  });
});
