import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISoignant } from '../soignant.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../soignant.test-samples';

import { SoignantService } from './soignant.service';

const requireRestSample: ISoignant = {
  ...sampleWithRequiredData,
};

describe('Soignant Service', () => {
  let service: SoignantService;
  let httpMock: HttpTestingController;
  let expectedResult: ISoignant | ISoignant[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SoignantService);
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

    it('should create a Soignant', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const soignant = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(soignant).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Soignant', () => {
      const soignant = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(soignant).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Soignant', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Soignant', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Soignant', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addSoignantToCollectionIfMissing', () => {
      it('should add a Soignant to an empty array', () => {
        const soignant: ISoignant = sampleWithRequiredData;
        expectedResult = service.addSoignantToCollectionIfMissing([], soignant);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(soignant);
      });

      it('should not add a Soignant to an array that contains it', () => {
        const soignant: ISoignant = sampleWithRequiredData;
        const soignantCollection: ISoignant[] = [
          {
            ...soignant,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSoignantToCollectionIfMissing(soignantCollection, soignant);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Soignant to an array that doesn't contain it", () => {
        const soignant: ISoignant = sampleWithRequiredData;
        const soignantCollection: ISoignant[] = [sampleWithPartialData];
        expectedResult = service.addSoignantToCollectionIfMissing(soignantCollection, soignant);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(soignant);
      });

      it('should add only unique Soignant to an array', () => {
        const soignantArray: ISoignant[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const soignantCollection: ISoignant[] = [sampleWithRequiredData];
        expectedResult = service.addSoignantToCollectionIfMissing(soignantCollection, ...soignantArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const soignant: ISoignant = sampleWithRequiredData;
        const soignant2: ISoignant = sampleWithPartialData;
        expectedResult = service.addSoignantToCollectionIfMissing([], soignant, soignant2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(soignant);
        expect(expectedResult).toContain(soignant2);
      });

      it('should accept null and undefined values', () => {
        const soignant: ISoignant = sampleWithRequiredData;
        expectedResult = service.addSoignantToCollectionIfMissing([], null, soignant, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(soignant);
      });

      it('should return initial array if no Soignant is added', () => {
        const soignantCollection: ISoignant[] = [sampleWithRequiredData];
        expectedResult = service.addSoignantToCollectionIfMissing(soignantCollection, undefined, null);
        expect(expectedResult).toEqual(soignantCollection);
      });
    });

    describe('compareSoignant', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSoignant(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareSoignant(entity1, entity2);
        const compareResult2 = service.compareSoignant(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareSoignant(entity1, entity2);
        const compareResult2 = service.compareSoignant(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareSoignant(entity1, entity2);
        const compareResult2 = service.compareSoignant(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
