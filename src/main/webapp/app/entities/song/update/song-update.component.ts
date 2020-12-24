import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder,Validators} from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { ISong, Song } from '../song.model';
import { SongService } from '../service/song.service';
import { IAlbum } from 'app/entities/album/album.model';
import { AlbumService } from 'app/entities/album/service/album.service';

@Component({
    selector: 'jhi-song-update',
    templateUrl: './song-update.component.html'
})
export class SongUpdateComponent implements OnInit {
    isSaving = false;
    albums: IAlbum[] = [];

    editForm = this.fb.group({
        id: [],
        name: [null,[Validators.required,]],
        url: [null,[Validators.required,]],
        releaseDate: [],
        album: [],
    });

    constructor(
        protected songService: SongService,
        protected albumService: AlbumService,
        protected activatedRoute: ActivatedRoute,
        private fb: FormBuilder,
    ) {}

    ngOnInit(): void {
        this.activatedRoute.data.subscribe(({ song }) => {

            this.updateForm(song);

            
                        this.albumService.query()
                            .subscribe((res: HttpResponse<IAlbum[]>) => this.albums = res.body ?? []);
        });
    }

    updateForm(song: ISong): void {
        this.editForm.patchValue({
            id: song.id,
            name: song.name,
            url: song.url,
            releaseDate: song.releaseDate,
            album: song.album,
        });
    }


    previousState(): void {
        window.history.back();
    }

    save(): void {
        this.isSaving = true;
        const song = this.createFromForm();
        if (song.id !== undefined) {
            this.subscribeToSaveResponse(
                this.songService.update(song));
        } else {
            this.subscribeToSaveResponse(
                this.songService.create(song));
        }
    }

    private createFromForm(): ISong {
        return {
            ...new Song(),
            id: this.editForm.get(['id'])!.value,
            name: this.editForm.get(['name'])!.value,
            url: this.editForm.get(['url'])!.value,
            releaseDate: this.editForm.get(['releaseDate'])!.value,
            album: this.editForm.get(['album'])!.value,
        };
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<ISong>>): void {
        result.subscribe(() => this.onSaveSuccess(), () => this.onSaveError());
    }

    protected onSaveSuccess(): void {
        this.isSaving = false;
        this.previousState();
    }

    protected onSaveError(): void {
        this.isSaving = false;
    }

    trackAlbumById(index: number, item: IAlbum): number {
        return item.id!;
    }
}
