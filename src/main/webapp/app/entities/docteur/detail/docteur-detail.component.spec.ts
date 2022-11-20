import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { DocteurDetailComponent } from './docteur-detail.component';

describe('Docteur Management Detail Component', () => {
  let comp: DocteurDetailComponent;
  let fixture: ComponentFixture<DocteurDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DocteurDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ docteur: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(DocteurDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(DocteurDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load docteur on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.docteur).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
