import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IRappel } from '../rappel.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../rappel.test-samples';

import { RappelService, RestRappel } from './rappel.service';

const requireRestSample: RestRappel = {
  ...sampleWithRequiredData,
  date: sampleWithRequiredData.date?.toJSON(),
};

describe('Rappel Service', () => {
  let service: RappelService;
  let httpMock: HttpTestingController;
  let expectedResult: IRappel | IRappel[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(RappelService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Rappel', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const rappel = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(rappel).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Rappel', () => {
      const rappel = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(rappel).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Rappel', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Rappel', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Rappel', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addRappelToCollectionIfMissing', () => {
      it('should add a Rappel to an empty array', () => {
        const rappel: IRappel = sampleWithRequiredData;
        expectedResult = service.addRappelToCollectionIfMissing([], rappel);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(rappel);
      });

      it('should not add a Rappel to an array that contains it', () => {
        const rappel: IRappel = sampleWithRequiredData;
        const rappelCollection: IRappel[] = [
          {
            ...rappel,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addRappelToCollectionIfMissing(rappelCollection, rappel);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Rappel to an array that doesn't contain it", () => {
        const rappel: IRappel = sampleWithRequiredData;
        const rappelCollection: IRappel[] = [sampleWithPartialData];
        expectedResult = service.addRappelToCollectionIfMissing(rappelCollection, rappel);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(rappel);
      });

      it('should add only unique Rappel to an array', () => {
        const rappelArray: IRappel[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const rappelCollection: IRappel[] = [sampleWithRequiredData];
        expectedResult = service.addRappelToCollectionIfMissing(rappelCollection, ...rappelArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const rappel: IRappel = sampleWithRequiredData;
        const rappel2: IRappel = sampleWithPartialData;
        expectedResult = service.addRappelToCollectionIfMissing([], rappel, rappel2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(rappel);
        expect(expectedResult).toContain(rappel2);
      });

      it('should accept null and undefined values', () => {
        const rappel: IRappel = sampleWithRequiredData;
        expectedResult = service.addRappelToCollectionIfMissing([], null, rappel, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(rappel);
      });

      it('should return initial array if no Rappel is added', () => {
        const rappelCollection: IRappel[] = [sampleWithRequiredData];
        expectedResult = service.addRappelToCollectionIfMissing(rappelCollection, undefined, null);
        expect(expectedResult).toEqual(rappelCollection);
      });
    });

    describe('compareRappel', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareRappel(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareRappel(entity1, entity2);
        const compareResult2 = service.compareRappel(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareRappel(entity1, entity2);
        const compareResult2 = service.compareRappel(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareRappel(entity1, entity2);
        const compareResult2 = service.compareRappel(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
