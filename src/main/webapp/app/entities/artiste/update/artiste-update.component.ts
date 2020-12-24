import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder,Validators} from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { IArtiste, Artiste } from '../artiste.model';
import { ArtisteService } from '../service/artiste.service';

@Component({
    selector: 'jhi-artiste-update',
    templateUrl: './artiste-update.component.html'
})
export class ArtisteUpdateComponent implements OnInit {
    isSaving = false;

    editForm = this.fb.group({
        id: [],
        firstName: [null,[Validators.required,]],
        lastName: [null,[Validators.required,]],
        dateNaiss: [null,[Validators.required,]],
    });

    constructor(
        protected artisteService: ArtisteService,
        protected activatedRoute: ActivatedRoute,
        private fb: FormBuilder,
    ) {}

    ngOnInit(): void {
        this.activatedRoute.data.subscribe(({ artiste }) => {

            this.updateForm(artiste);

        });
    }

    updateForm(artiste: IArtiste): void {
        this.editForm.patchValue({
            id: artiste.id,
            firstName: artiste.firstName,
            lastName: artiste.lastName,
            dateNaiss: artiste.dateNaiss,
        });
    }


    previousState(): void {
        window.history.back();
    }

    save(): void {
        this.isSaving = true;
        const artiste = this.createFromForm();
        if (artiste.id !== undefined) {
            this.subscribeToSaveResponse(
                this.artisteService.update(artiste));
        } else {
            this.subscribeToSaveResponse(
                this.artisteService.create(artiste));
        }
    }

    private createFromForm(): IArtiste {
        return {
            ...new Artiste(),
            id: this.editForm.get(['id'])!.value,
            firstName: this.editForm.get(['firstName'])!.value,
            lastName: this.editForm.get(['lastName'])!.value,
            dateNaiss: this.editForm.get(['dateNaiss'])!.value,
        };
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<IArtiste>>): void {
        result.subscribe(() => this.onSaveSuccess(), () => this.onSaveError());
    }

    protected onSaveSuccess(): void {
        this.isSaving = false;
        this.previousState();
    }

    protected onSaveError(): void {
        this.isSaving = false;
    }
}
