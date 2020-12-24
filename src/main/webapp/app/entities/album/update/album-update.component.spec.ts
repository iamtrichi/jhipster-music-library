jest.mock('@angular/router');

import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AlbumService } from '../service/album.service';
import { Album } from '../album.model';
import { Artiste } from 'app/entities/artiste/artiste.model';

import { AlbumUpdateComponent } from './album-update.component';

describe('Component Tests', () => {
    describe('Album Management Update Component', () => {
        let comp: AlbumUpdateComponent;
        let fixture: ComponentFixture<AlbumUpdateComponent>;
        let service: AlbumService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [HttpClientTestingModule],
                declarations: [AlbumUpdateComponent],
                providers: [FormBuilder, ActivatedRoute]
            })
            .overrideTemplate(AlbumUpdateComponent, '')
            .compileComponents();

            fixture = TestBed.createComponent(AlbumUpdateComponent);
            comp = fixture.componentInstance;
            service = TestBed.inject(AlbumService);
        });

        describe('save', () => {
            it('Should call update service on save for existing entity',
                fakeAsync(() => {
                    // GIVEN
                    const entity = new Album(123);
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
                    const entity = new Album();
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

        describe('Tracking relationships identifiers', () => {
            describe('trackArtisteById', () => {
                it('Should return tracked Artiste primary key', () => {
                    const entity = new Artiste(123);
                    const trackResult = comp.trackArtisteById(0, entity);
                    expect(trackResult).toEqual(entity.id);
                });
            });

        });
    });
});
