import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IExamen } from '../examen.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../examen.test-samples';

import { ExamenService, RestExamen } from './examen.service';

const requireRestSample: RestExamen = {
  ...sampleWithRequiredData,
  date: sampleWithRequiredData.date?.toJSON(),
};

describe('Examen Service', () => {
  let service: ExamenService;
  let httpMock: HttpTestingController;
  let expectedResult: IExamen | IExamen[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ExamenService);
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

    it('should create a Examen', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const examen = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(examen).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Examen', () => {
      const examen = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(examen).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Examen', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Examen', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Examen', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addExamenToCollectionIfMissing', () => {
      it('should add a Examen to an empty array', () => {
        const examen: IExamen = sampleWithRequiredData;
        expectedResult = service.addExamenToCollectionIfMissing([], examen);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(examen);
      });

      it('should not add a Examen to an array that contains it', () => {
        const examen: IExamen = sampleWithRequiredData;
        const examenCollection: IExamen[] = [
          {
            ...examen,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addExamenToCollectionIfMissing(examenCollection, examen);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Examen to an array that doesn't contain it", () => {
        const examen: IExamen = sampleWithRequiredData;
        const examenCollection: IExamen[] = [sampleWithPartialData];
        expectedResult = service.addExamenToCollectionIfMissing(examenCollection, examen);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(examen);
      });

      it('should add only unique Examen to an array', () => {
        const examenArray: IExamen[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const examenCollection: IExamen[] = [sampleWithRequiredData];
        expectedResult = service.addExamenToCollectionIfMissing(examenCollection, ...examenArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const examen: IExamen = sampleWithRequiredData;
        const examen2: IExamen = sampleWithPartialData;
        expectedResult = service.addExamenToCollectionIfMissing([], examen, examen2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(examen);
        expect(expectedResult).toContain(examen2);
      });

      it('should accept null and undefined values', () => {
        const examen: IExamen = sampleWithRequiredData;
        expectedResult = service.addExamenToCollectionIfMissing([], null, examen, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(examen);
      });

      it('should return initial array if no Examen is added', () => {
        const examenCollection: IExamen[] = [sampleWithRequiredData];
        expectedResult = service.addExamenToCollectionIfMissing(examenCollection, undefined, null);
        expect(expectedResult).toEqual(examenCollection);
      });
    });

    describe('compareExamen', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareExamen(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareExamen(entity1, entity2);
        const compareResult2 = service.compareExamen(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareExamen(entity1, entity2);
        const compareResult2 = service.compareExamen(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareExamen(entity1, entity2);
        const compareResult2 = service.compareExamen(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
