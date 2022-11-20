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

describe('Examen e2e test', () => {
  const examenPageUrl = '/examen';
  const examenPageUrlPattern = new RegExp('/examen(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const examenSample = { date: '2022-11-20T11:08:39.203Z' };

  let examen;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/examen+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/examen').as('postEntityRequest');
    cy.intercept('DELETE', '/api/examen/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (examen) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/examen/${examen.id}`,
      }).then(() => {
        examen = undefined;
      });
    }
  });

  it('Examen menu should load Examen page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('examen');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Examen').should('exist');
    cy.url().should('match', examenPageUrlPattern);
  });

  describe('Examen page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(examenPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Examen page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/examen/new$'));
        cy.getEntityCreateUpdateHeading('Examen');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', examenPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/examen',
          body: examenSample,
        }).then(({ body }) => {
          examen = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/examen+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [examen],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(examenPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Examen page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('examen');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', examenPageUrlPattern);
      });

      it('edit button click should load edit Examen page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Examen');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', examenPageUrlPattern);
      });

      it.skip('edit button click should load edit Examen page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Examen');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', examenPageUrlPattern);
      });

      it('last delete button click should delete instance of Examen', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('examen').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', examenPageUrlPattern);

        examen = undefined;
      });
    });
  });

  describe('new Examen page', () => {
    beforeEach(() => {
      cy.visit(`${examenPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Examen');
    });

    it('should create an instance of Examen', () => {
      cy.get(`[data-cy="date"]`).type('2022-11-19T21:00').blur().should('have.value', '2022-11-19T21:00');

      cy.get(`[data-cy="poids"]`).type('63461').should('have.value', '63461');

      cy.get(`[data-cy="albumine"]`).type('25150').should('have.value', '25150');

      cy.get(`[data-cy="imc"]`).type('83962').should('have.value', '83962');

      cy.get(`[data-cy="epa"]`).type('6').should('have.value', '6');

      cy.get(`[data-cy="commentaire"]`).type('SQL generate').should('have.value', 'SQL generate');

      cy.get(`[data-cy="deleted"]`).should('not.be.checked');
      cy.get(`[data-cy="deleted"]`).click().should('be.checked');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        examen = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', examenPageUrlPattern);
    });
  });
});
