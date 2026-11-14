
/* Muly - Service Worker */

const CACHE_NAME = "muly-cache-v1";

// Kurulum
self.addEventListener("install", (event) => {
  self.skipWaiting();
});

// Etkinleşme
self.addEventListener("activate", (event) => {
  event.waitUntil(self.clients.claim());
});

// PUSH BİLDİRİMLERİ
self.addEventListener("push", (event) => {
  let data = {};

  try {
    data = event.data ? event.data.json() : {};
  } catch (error) {
    data = {
      title: "Muly 💗",
      body: event.data
        ? event.data.text()
        : "Yeni bildirimin var!"
    };
  }

  const title = data.title || "Muly 💗";

  const options = {
    body: data.body || "Sana bir bildirim geldi! ❤️",
    icon: "/icon-192.png",
    badge: "/icon-192.png",
    vibrate: [200, 100, 200],
    tag: data.tag || "muly-notification",
    renotify: true,
    data: {
      url: data.url || "/"
    }
  };

  event.waitUntil(
    self.registration.showNotification(title, options)
  );
});

// BİLDİRİME TIKLAMA
self.addEventListener("notificationclick", (event) => {
  event.notification.close();

  const targetUrl = new URL(
    event.notification.data?.url || "/",
    self.location.origin
  );

  // Yalnızca Muly adresine yönlendir
  if (targetUrl.origin !== self.location.origin) {
    return;
  }

  event.waitUntil(
    (async () => {
      const windows = await self.clients.matchAll({
        type: "window",
        includeUncontrolled: true
      });

      for (const client of windows) {
        if ("focus" in client) {
          await client.navigate(targetUrl.href);
          return client.focus();
        }
      }

      return self.clients.openWindow(targetUrl.href);
    })()
  );
});
