
import * as dayjs from 'dayjs';
import { ISong } from 'app/entities/song/song.model';
import { IArtiste } from 'app/entities/artiste/artiste.model';

export interface IAlbum {
    id?: number;
    title?: string;
    releaseDate?: dayjs.Dayjs;
    songs?: ISong[];
    artiste?: IArtiste;
}

export class Album implements IAlbum {
    constructor(
        public id?: number,
        public title?: string,
        public releaseDate?: dayjs.Dayjs,
        public songs?: ISong[],
        public artiste?: IArtiste,
    ) {
    }
}
