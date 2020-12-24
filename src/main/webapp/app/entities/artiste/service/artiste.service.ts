import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/core/request/request-util';
import { IArtiste } from '../artiste.model';

type EntityResponseType = HttpResponse<IArtiste>;
type EntityArrayResponseType = HttpResponse<IArtiste[]>;

@Injectable({ providedIn: 'root' })
export class ArtisteService {
    public resourceUrl = SERVER_API_URL + 'api/artistes';

    constructor(protected http: HttpClient) {}

    create(artiste: IArtiste): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(artiste);
        return this.http.post<IArtiste>(this.resourceUrl,
                 copy ,
                { observe: 'response' })
        .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    update(artiste: IArtiste): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(artiste);
        return this.http.put<IArtiste>(this.resourceUrl,
                 copy ,
                { observe: 'response' })
        .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<IArtiste>(`${this.resourceUrl}/${id}`, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IArtiste[]>(this.resourceUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
    }

    delete(id: number): Observable<HttpResponse<{}>> {
        return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }


    protected convertDateFromClient(artiste: IArtiste): IArtiste {
        const copy: IArtiste = Object.assign({}, artiste, {
        dateNaiss: artiste.dateNaiss?.isValid() ? artiste.dateNaiss.format(DATE_FORMAT) : undefined,
        });
        return copy;
    }

    protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
        if (res.body) {
            res.body.dateNaiss = res.body.dateNaiss ? dayjs(res.body.dateNaiss) : undefined;
        }
        return res;
    }

    protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
        if (res.body) {
            res.body.forEach ((artiste: IArtiste) => {
                artiste.dateNaiss = artiste.dateNaiss ? dayjs(artiste.dateNaiss) : undefined;
            });
        }
        return res;
    }
}
