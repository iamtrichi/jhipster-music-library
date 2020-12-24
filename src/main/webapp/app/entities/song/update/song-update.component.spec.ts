jest.mock('@angular/router');

import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SongService } from '../service/song.service';
import { Song } from '../song.model';
import { Album } from 'app/entities/album/album.model';

import { SongUpdateComponent } from './song-update.component';

describe('Component Tests', () => {
    describe('Song Management Update Component', () => {
        let comp: SongUpdateComponent;
        let fixture: ComponentFixture<SongUpdateComponent>;
        let service: SongService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [HttpClientTestingModule],
                declarations: [SongUpdateComponent],
                providers: [FormBuilder, ActivatedRoute]
            })
            .overrideTemplate(SongUpdateComponent, '')
            .compileComponents();

            fixture = TestBed.createComponent(SongUpdateComponent);
            comp = fixture.componentInstance;
            service = TestBed.inject(SongService);
        });

        describe('save', () => {
            it('Should call update service on save for existing entity',
                fakeAsync(() => {
                    // GIVEN
                    const entity = new Song(123);
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
                    const entity = new Song();
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
            describe('trackAlbumById', () => {
                it('Should return tracked Album primary key', () => {
                    const entity = new Album(123);
                    const trackResult = comp.trackAlbumById(0, entity);
                    expect(trackResult).toEqual(entity.id);
                });
            });

        });
    });
});
