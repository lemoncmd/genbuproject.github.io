package org.simpleframework.xml.core;

class SessionManager {
    private ThreadLocal<Reference> local = new ThreadLocal();

    private static class Reference {
        private int count;
        private Session session;

        public Reference(boolean z) {
            this.session = new Session(z);
        }

        public int clear() {
            int i = this.count - 1;
            this.count = i;
            return i;
        }

        public Session get() {
            if (this.count >= 0) {
                this.count++;
            }
            return this.session;
        }
    }

    private Session create(boolean z) throws Exception {
        Reference reference = new Reference(z);
        if (reference != null) {
            this.local.set(reference);
        }
        return reference.get();
    }

    public void close() throws Exception {
        Reference reference = (Reference) this.local.get();
        if (reference == null) {
            throw new PersistenceException("Session does not exist", new Object[0]);
        } else if (reference.clear() == 0) {
            this.local.remove();
        }
    }

    public Session open() throws Exception {
        return open(true);
    }

    public Session open(boolean z) throws Exception {
        Reference reference = (Reference) this.local.get();
        return reference != null ? reference.get() : create(z);
    }
}
