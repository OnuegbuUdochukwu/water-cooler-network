// PWA utilities for service worker registration and app installation

export interface BeforeInstallPromptEvent extends Event {
  readonly platforms: string[];
  readonly userChoice: Promise<{
    outcome: 'accepted' | 'dismissed';
    platform: string;
  }>;
  prompt(): Promise<void>;
}

class PWAManager {
  private deferredPrompt: BeforeInstallPromptEvent | null = null;
  private isInstalled = false;

  constructor() {
    this.init();
  }

  private init() {
    // Register service worker
    this.registerServiceWorker();
    
    // Listen for install prompt
    this.setupInstallPrompt();
    
    // Check if app is already installed
    this.checkInstallStatus();
  }

  private async registerServiceWorker() {
    if ('serviceWorker' in navigator) {
      try {
        const registration = await navigator.serviceWorker.register('/sw.js');
        console.log('Service Worker registered successfully:', registration);
        
        // Listen for updates
        registration.addEventListener('updatefound', () => {
          const newWorker = registration.installing;
          if (newWorker) {
            newWorker.addEventListener('statechange', () => {
              if (newWorker.state === 'installed' && navigator.serviceWorker.controller) {
                // New content available, notify user
                this.notifyUpdate();
              }
            });
          }
        });
      } catch (error) {
        console.error('Service Worker registration failed:', error);
      }
    }
  }

  private setupInstallPrompt() {
    window.addEventListener('beforeinstallprompt', (e: Event) => {
      e.preventDefault();
      this.deferredPrompt = e as BeforeInstallPromptEvent;
      this.showInstallButton();
    });

    window.addEventListener('appinstalled', () => {
      this.isInstalled = true;
      this.hideInstallButton();
      console.log('PWA was installed');
    });
  }

  private checkInstallStatus() {
    // Check if app is running in standalone mode
    if (window.matchMedia('(display-mode: standalone)').matches) {
      this.isInstalled = true;
    }
  }

  public async installApp(): Promise<boolean> {
    if (!this.deferredPrompt) {
      return false;
    }

    try {
      await this.deferredPrompt.prompt();
      const choiceResult = await this.deferredPrompt.userChoice;
      
      if (choiceResult.outcome === 'accepted') {
        console.log('User accepted the install prompt');
        this.deferredPrompt = null;
        return true;
      } else {
        console.log('User dismissed the install prompt');
        return false;
      }
    } catch (error) {
      console.error('Error during app installation:', error);
      return false;
    }
  }

  private showInstallButton() {
    // Create install button if it doesn't exist
    let installButton = document.getElementById('pwa-install-button');
    
    if (!installButton) {
      installButton = document.createElement('button');
      installButton.id = 'pwa-install-button';
      installButton.innerHTML = '📱 Install App';
      installButton.className = 'pwa-install-btn';
      installButton.onclick = () => this.installApp();
      
      // Add to navbar or create floating button
      const navbar = document.querySelector('.navbar-menu');
      if (navbar) {
        navbar.appendChild(installButton);
      } else {
        // Create floating install button
        installButton.style.cssText = `
          position: fixed;
          bottom: 20px;
          right: 20px;
          z-index: 1000;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          color: white;
          border: none;
          padding: 12px 20px;
          border-radius: 25px;
          font-size: 14px;
          font-weight: 600;
          cursor: pointer;
          box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
          transition: all 0.3s ease;
        `;
        document.body.appendChild(installButton);
      }
    }
    
    installButton.style.display = 'block';
  }

  private hideInstallButton() {
    const installButton = document.getElementById('pwa-install-button');
    if (installButton) {
      installButton.style.display = 'none';
    }
  }

  private notifyUpdate() {
    // Show update notification
    const updateNotification = document.createElement('div');
    updateNotification.className = 'update-notification';
    updateNotification.innerHTML = `
      <div class="update-content">
        <span>🔄 New version available!</span>
        <button onclick="window.location.reload()" class="update-btn">Update</button>
      </div>
    `;
    updateNotification.style.cssText = `
      position: fixed;
      top: 20px;
      right: 20px;
      background: white;
      border-radius: 8px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      padding: 16px;
      z-index: 10000;
      display: flex;
      align-items: center;
      gap: 12px;
    `;
    
    document.body.appendChild(updateNotification);
    
    // Auto-remove after 10 seconds
    setTimeout(() => {
      if (updateNotification.parentNode) {
        updateNotification.parentNode.removeChild(updateNotification);
      }
    }, 10000);
  }

  public isAppInstalled(): boolean {
    return this.isInstalled;
  }

  public canInstall(): boolean {
    return this.deferredPrompt !== null;
  }
}

// Export singleton instance
export const pwaManager = new PWAManager();

// Utility functions for mobile detection and optimization
export const isMobile = (): boolean => {
  return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent);
};

export const isIOS = (): boolean => {
  return /iPad|iPhone|iPod/.test(navigator.userAgent);
};

export const isAndroid = (): boolean => {
  return /Android/.test(navigator.userAgent);
};

export const getViewportHeight = (): number => {
  return window.visualViewport?.height || window.innerHeight;
};

export const handleViewportChange = (callback: (height: number) => void): void => {
  if (window.visualViewport) {
    window.visualViewport.addEventListener('resize', () => {
      callback(getViewportHeight());
    });
  } else {
    window.addEventListener('resize', () => {
      callback(getViewportHeight());
    });
  }
};

// Touch gesture utilities
export const addSwipeGesture = (
  element: HTMLElement,
  onSwipeLeft?: () => void,
  onSwipeRight?: () => void,
  onSwipeUp?: () => void,
  onSwipeDown?: () => void
): void => {
  let startX = 0;
  let startY = 0;
  let endX = 0;
  let endY = 0;

  element.addEventListener('touchstart', (e) => {
    startX = e.touches[0].clientX;
    startY = e.touches[0].clientY;
  });

  element.addEventListener('touchend', (e) => {
    endX = e.changedTouches[0].clientX;
    endY = e.changedTouches[0].clientY;
    handleSwipe();
  });

  const handleSwipe = () => {
    const deltaX = endX - startX;
    const deltaY = endY - startY;
    const minSwipeDistance = 50;

    if (Math.abs(deltaX) > Math.abs(deltaY)) {
      // Horizontal swipe
      if (Math.abs(deltaX) > minSwipeDistance) {
        if (deltaX > 0 && onSwipeRight) {
          onSwipeRight();
        } else if (deltaX < 0 && onSwipeLeft) {
          onSwipeLeft();
        }
      }
    } else {
      // Vertical swipe
      if (Math.abs(deltaY) > minSwipeDistance) {
        if (deltaY > 0 && onSwipeDown) {
          onSwipeDown();
        } else if (deltaY < 0 && onSwipeUp) {
          onSwipeUp();
        }
      }
    }
  };
};
