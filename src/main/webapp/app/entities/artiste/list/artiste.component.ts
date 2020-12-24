import { Component, OnInit } from '@angular/core';
import {HttpHeaders, HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IArtiste } from '../artiste.model';

import { ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { ArtisteService } from '../service/artiste.service';
import { ArtisteDeleteDialogComponent } from '../delete/artiste-delete-dialog.component';
import { ParseLinks } from 'app/core/util/parse-links.service';

@Component({
    selector: 'jhi-artiste',
    templateUrl: './artiste.component.html'
})
export class ArtisteComponent implements OnInit {
    artistes: IArtiste[];
    isLoading = false;
    itemsPerPage: number;
    links: { [key: string]: number };
    page: number;
    predicate: string;
    ascending: boolean;

    constructor(
        protected artisteService: ArtisteService,
        protected modalService: NgbModal,
        protected parseLinks: ParseLinks,
    ) {
        this.artistes = [];
        this.itemsPerPage = ITEMS_PER_PAGE;
        this.page = 0;
        this.links = {
            last: 0
        };
        this.predicate = 'id';
        this.ascending = true;
    }

    loadAll(): void {
        this.isLoading = true;

        this.artisteService.query({
            page: this.page,
            size: this.itemsPerPage,
            sort: this.sort()
        }).subscribe(
            (res: HttpResponse<IArtiste[]>) => {
                this.isLoading = false;
                this.paginateArtistes(res.body, res.headers);
            },
            () => {
                this.isLoading = false;
            }
        );
    }

    reset(): void {
        this.page = 0;
        this.artistes = [];
        this.loadAll();
    }

    loadPage(page: number): void {
        this.page = page;
        this.loadAll();
    }


    ngOnInit(): void {
        this.loadAll();
    }

    trackId(index: number, item: IArtiste): number {
        return item.id!;
    }



    delete(artiste: IArtiste): void {
        const modalRef = this.modalService.open(ArtisteDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
        modalRef.componentInstance.artiste = artiste;
        // unsubscribe not needed because closed completes on modal close
        modalRef.closed.subscribe(reason => {
            if (reason === 'deleted') {
                this.reset();
            }
        });
    }

    sort(): string[] {
        const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
        if (this.predicate !== 'id') {
            result.push('id');
        }
        return result;
    }


    protected paginateArtistes(data: IArtiste[] | null, headers: HttpHeaders): void {
        this.links = this.parseLinks.parse(headers.get('link') ?? '');
        if (data) {
            for (let i = 0; i < data.length; i++) {
                this.artistes.push(data[i]);
            }
        }
    }

}
