import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SoignantDetailComponent } from './soignant-detail.component';

describe('Soignant Management Detail Component', () => {
  let comp: SoignantDetailComponent;
  let fixture: ComponentFixture<SoignantDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SoignantDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ soignant: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(SoignantDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(SoignantDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load soignant on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.soignant).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
