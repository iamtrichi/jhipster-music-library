import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'artiste',
        data: { pageTitle: 'musicLibraryApp.artiste.home.title' },
        loadChildren: () => import('./artiste/artiste.module').then(m => m.ArtisteModule),
      },
      {
        path: 'album',
        data: { pageTitle: 'musicLibraryApp.album.home.title' },
        loadChildren: () => import('./album/album.module').then(m => m.AlbumModule),
      },
      {
        path: 'song',
        data: { pageTitle: 'musicLibraryApp.song.home.title' },
        loadChildren: () => import('./song/song.module').then(m => m.SongModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
