// Muly PWA — yalnızca uygulama kabuğu; kullanıcı verilerini önbelleğe almaz.
self.addEventListener('install', () => self.skipWaiting());
self.addEventListener('activate', event => event.waitUntil(self.clients.claim()));
// Web Push sonraki aşamada güvenli sunucu aboneliği kurulduğunda etkinleştirilecek.
