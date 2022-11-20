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

describe('Soignant e2e test', () => {
  const soignantPageUrl = '/soignant';
  const soignantPageUrlPattern = new RegExp('/soignant(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const soignantSample = {};

  let soignant;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/soignants+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/soignants').as('postEntityRequest');
    cy.intercept('DELETE', '/api/soignants/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (soignant) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/soignants/${soignant.id}`,
      }).then(() => {
        soignant = undefined;
      });
    }
  });

  it('Soignants menu should load Soignants page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('soignant');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Soignant').should('exist');
    cy.url().should('match', soignantPageUrlPattern);
  });

  describe('Soignant page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(soignantPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Soignant page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/soignant/new$'));
        cy.getEntityCreateUpdateHeading('Soignant');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', soignantPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/soignants',
          body: soignantSample,
        }).then(({ body }) => {
          soignant = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/soignants+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [soignant],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(soignantPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Soignant page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('soignant');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', soignantPageUrlPattern);
      });

      it('edit button click should load edit Soignant page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Soignant');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', soignantPageUrlPattern);
      });

      it.skip('edit button click should load edit Soignant page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Soignant');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', soignantPageUrlPattern);
      });

      it('last delete button click should delete instance of Soignant', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('soignant').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', soignantPageUrlPattern);

        soignant = undefined;
      });
    });
  });

  describe('new Soignant page', () => {
    beforeEach(() => {
      cy.visit(`${soignantPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Soignant');
    });

    it('should create an instance of Soignant', () => {
      cy.get(`[data-cy="deleted"]`).should('not.be.checked');
      cy.get(`[data-cy="deleted"]`).click().should('be.checked');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        soignant = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', soignantPageUrlPattern);
    });
  });
});
