import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { DocteurService } from '../service/docteur.service';

import { DocteurComponent } from './docteur.component';

describe('Docteur Management Component', () => {
  let comp: DocteurComponent;
  let fixture: ComponentFixture<DocteurComponent>;
  let service: DocteurService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'docteur', component: DocteurComponent }]), HttpClientTestingModule],
      declarations: [DocteurComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
                'filter[someId.in]': 'dc4279ea-cfb9-11ec-9d64-0242ac120002',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(DocteurComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DocteurComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(DocteurService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.docteurs?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to docteurService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getDocteurIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getDocteurIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
