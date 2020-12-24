jest.mock('@angular/router');

import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ArtisteService } from '../service/artiste.service';
import { Artiste } from '../artiste.model';

import { ArtisteUpdateComponent } from './artiste-update.component';

describe('Component Tests', () => {
    describe('Artiste Management Update Component', () => {
        let comp: ArtisteUpdateComponent;
        let fixture: ComponentFixture<ArtisteUpdateComponent>;
        let service: ArtisteService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [HttpClientTestingModule],
                declarations: [ArtisteUpdateComponent],
                providers: [FormBuilder, ActivatedRoute]
            })
            .overrideTemplate(ArtisteUpdateComponent, '')
            .compileComponents();

            fixture = TestBed.createComponent(ArtisteUpdateComponent);
            comp = fixture.componentInstance;
            service = TestBed.inject(ArtisteService);
        });

        describe('save', () => {
            it('Should call update service on save for existing entity',
                fakeAsync(() => {
                    // GIVEN
                    const entity = new Artiste(123);
                    spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.updateForm(entity);
                    // WHEN
                    comp.save();
                    tick(); // simulate async

                    // THEN
                    expect(service.update).toHaveBeenCalledWith(entity);
                    expect(comp.isSaving).toEqual(false);
                })
            );

            it('Should call create service on save for new entity',
                fakeAsync(() => {
                    // GIVEN
                    const entity = new Artiste();
                    spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.updateForm(entity);
                    // WHEN
                    comp.save();
                    tick(); // simulate async

                    // THEN
                    expect(service.create).toHaveBeenCalledWith(entity);
                    expect(comp.isSaving).toEqual(false);
                })
            );
        });
    });
});
