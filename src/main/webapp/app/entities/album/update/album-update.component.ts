import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder,Validators} from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { IAlbum, Album } from '../album.model';
import { AlbumService } from '../service/album.service';
import { IArtiste } from 'app/entities/artiste/artiste.model';
import { ArtisteService } from 'app/entities/artiste/service/artiste.service';

@Component({
    selector: 'jhi-album-update',
    templateUrl: './album-update.component.html'
})
export class AlbumUpdateComponent implements OnInit {
    isSaving = false;
    artistes: IArtiste[] = [];

    editForm = this.fb.group({
        id: [],
        title: [null,[Validators.required,]],
        releaseDate: [],
        artiste: [],
    });

    constructor(
        protected albumService: AlbumService,
        protected artisteService: ArtisteService,
        protected activatedRoute: ActivatedRoute,
        private fb: FormBuilder,
    ) {}

    ngOnInit(): void {
        this.activatedRoute.data.subscribe(({ album }) => {

            this.updateForm(album);

            
                        this.artisteService.query()
                            .subscribe((res: HttpResponse<IArtiste[]>) => this.artistes = res.body ?? []);
        });
    }

    updateForm(album: IAlbum): void {
        this.editForm.patchValue({
            id: album.id,
            title: album.title,
            releaseDate: album.releaseDate,
            artiste: album.artiste,
        });
    }


    previousState(): void {
        window.history.back();
    }

    save(): void {
        this.isSaving = true;
        const album = this.createFromForm();
        if (album.id !== undefined) {
            this.subscribeToSaveResponse(
                this.albumService.update(album));
        } else {
            this.subscribeToSaveResponse(
                this.albumService.create(album));
        }
    }

    private createFromForm(): IAlbum {
        return {
            ...new Album(),
            id: this.editForm.get(['id'])!.value,
            title: this.editForm.get(['title'])!.value,
            releaseDate: this.editForm.get(['releaseDate'])!.value,
            artiste: this.editForm.get(['artiste'])!.value,
        };
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<IAlbum>>): void {
        result.subscribe(() => this.onSaveSuccess(), () => this.onSaveError());
    }

    protected onSaveSuccess(): void {
        this.isSaving = false;
        this.previousState();
    }

    protected onSaveError(): void {
        this.isSaving = false;
    }

    trackArtisteById(index: number, item: IArtiste): number {
        return item.id!;
    }
}
