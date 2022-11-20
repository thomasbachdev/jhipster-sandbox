import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IResident } from '../resident.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../resident.test-samples';

import { ResidentService, RestResident } from './resident.service';

const requireRestSample: RestResident = {
  ...sampleWithRequiredData,
  dateNaissance: sampleWithRequiredData.dateNaissance?.format(DATE_FORMAT),
  dateArrivee: sampleWithRequiredData.dateArrivee?.format(DATE_FORMAT),
};

describe('Resident Service', () => {
  let service: ResidentService;
  let httpMock: HttpTestingController;
  let expectedResult: IResident | IResident[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ResidentService);
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

    it('should create a Resident', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const resident = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(resident).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Resident', () => {
      const resident = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(resident).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Resident', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Resident', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Resident', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addResidentToCollectionIfMissing', () => {
      it('should add a Resident to an empty array', () => {
        const resident: IResident = sampleWithRequiredData;
        expectedResult = service.addResidentToCollectionIfMissing([], resident);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(resident);
      });

      it('should not add a Resident to an array that contains it', () => {
        const resident: IResident = sampleWithRequiredData;
        const residentCollection: IResident[] = [
          {
            ...resident,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addResidentToCollectionIfMissing(residentCollection, resident);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Resident to an array that doesn't contain it", () => {
        const resident: IResident = sampleWithRequiredData;
        const residentCollection: IResident[] = [sampleWithPartialData];
        expectedResult = service.addResidentToCollectionIfMissing(residentCollection, resident);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(resident);
      });

      it('should add only unique Resident to an array', () => {
        const residentArray: IResident[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const residentCollection: IResident[] = [sampleWithRequiredData];
        expectedResult = service.addResidentToCollectionIfMissing(residentCollection, ...residentArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const resident: IResident = sampleWithRequiredData;
        const resident2: IResident = sampleWithPartialData;
        expectedResult = service.addResidentToCollectionIfMissing([], resident, resident2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(resident);
        expect(expectedResult).toContain(resident2);
      });

      it('should accept null and undefined values', () => {
        const resident: IResident = sampleWithRequiredData;
        expectedResult = service.addResidentToCollectionIfMissing([], null, resident, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(resident);
      });

      it('should return initial array if no Resident is added', () => {
        const residentCollection: IResident[] = [sampleWithRequiredData];
        expectedResult = service.addResidentToCollectionIfMissing(residentCollection, undefined, null);
        expect(expectedResult).toEqual(residentCollection);
      });
    });

    describe('compareResident', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareResident(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareResident(entity1, entity2);
        const compareResult2 = service.compareResident(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareResident(entity1, entity2);
        const compareResult2 = service.compareResident(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareResident(entity1, entity2);
        const compareResult2 = service.compareResident(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
