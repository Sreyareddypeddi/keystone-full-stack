import { useEffect, useState } from 'react';
import { Navigate, Route, Routes, useNavigate } from 'react-router-dom';
import {
  LayoutDashboard,
  ClipboardList,
  Users,
  MapPin,
  LogOut,
  Wrench,
  ShieldCheck,
  Clock,
  CheckCircle2,
  PauseCircle,
  PlayCircle,
  UserCheck,
  X,
} from 'lucide-react';

import { api, login } from './api';
import type { Role, WO } from './types';

function useAuth() {
  const [role, setRole] = useState<Role | null>(
    localStorage.getItem('role') as Role | null
  );

  const logout = () => {
    localStorage.clear();
    setRole(null);
  };

  return { role, setRole, logout };
}

function App() {
  const auth = useAuth();

  if (!auth.role) {
    return (
      <Login
        onLogin={(r) => auth.setRole(r as Role)}
      />
    );
  }

  return (
    <Shell
      role={auth.role}
      logout={auth.logout}
    />
  );
}

function Login({
  onLogin,
}: {
  onLogin: (r: string) => void;
}) {
  const nav = useNavigate();

  const [email, setEmail] = useState(
    'manager@keystone.local'
  );

  const [password, setPassword] = useState(
    'password'
  );

  const [error, setError] = useState('');

  async function submit(e: any) {
    e.preventDefault();

    setError('');

    try {
      const { data } = await login(
        email,
        password
      );

      localStorage.setItem(
        'token',
        data.token
      );

      localStorage.setItem(
        'role',
        data.role
      );

      localStorage.setItem(
        'name',
        data.name
      );

      if (data.customerId !== undefined) {
        localStorage.setItem(
          'customerId',
          data.customerId == null
            ? ''
            : String(data.customerId)
        );
      }

      onLogin(data.role);

      nav('/');
    } catch (err: any) {
      setError(
        err?.response?.data?.message ||
          'Login failed'
      );
    }
  }

  return (
    <div className="login-page">
      <div className="login-card">

        <div className="brand-mark">
          K
        </div>

        <p className="eyebrow">
          MERIDIAN FACILITIES
        </p>

        <h1>
          Welcome to KEYSTONE
        </h1>

        <p className="muted">
          Field service operations, in one place.
        </p>

        <form onSubmit={submit}>

          <label>
            Email

            <input
              value={email}
              onChange={(e) =>
                setEmail(e.target.value)
              }
            />
          </label>

          <label>
            Password

            <input
              type="password"
              value={password}
              onChange={(e) =>
                setPassword(e.target.value)
              }
            />
          </label>

          {error && (
            <div className="error">
              {error}
            </div>
          )}

          <button
            className="primary full"
            type="submit"
          >
            Sign in
          </button>

        </form>

        <div className="demo">
          Demo password:{' '}
          <b>password</b>
        </div>

      </div>
    </div>
  );
}

function Shell({
  role,
  logout,
}: {
  role: Role;
  logout: () => void;
}) {
  const nav = useNavigate();

  const links =
    role === 'CUSTOMER'
      ? [
          [
            '/',
            LayoutDashboard,
            'Portal',
          ],
          [
            '/work-orders',
            ClipboardList,
            'Requests',
          ],
        ]
      : role === 'TECHNICIAN'
      ? [
          [
            '/',
            LayoutDashboard,
            'Field View',
          ],
          [
            '/work-orders',
            ClipboardList,
            'My Jobs',
          ],
        ]
      : [
          [
            '/',
            LayoutDashboard,
            'Dashboard',
          ],
          [
            '/work-orders',
            ClipboardList,
            'Work Orders',
          ],
          [
            '/customers',
            Users,
            'Customers',
          ],
          [
            '/sites',
            MapPin,
            'Sites',
          ],
        ];

  return (
    <div className="app">

      <aside>

        <div className="logo">

          <span>K</span>

          <div>
            <b>KEYSTONE</b>
            <small>
              FIELD SERVICE
            </small>
          </div>

        </div>

        <nav>

          {links.map(
            ([p, I, l]: any) => (
              <button
                key={p}
                className="nav-item"
                onClick={() => nav(p)}
              >
                <I size={18} />
                {l}
              </button>
            )
          )}

        </nav>

        <div className="side-bottom">

          <div className="user-chip">

            <div className="avatar">
              {(
                localStorage.getItem(
                  'name'
                ) || 'U'
              )[0]}
            </div>

            <div>
              <b>
                {localStorage.getItem(
                  'name'
                )}
              </b>

              <small>
                {role}
              </small>
            </div>

          </div>

          <button
            className="nav-item"
            onClick={logout}
          >
            <LogOut size={18} />
            Sign out
          </button>

        </div>

      </aside>

      <main>

        <header>

          <div>

            <p className="eyebrow">
              OPERATIONS CENTER
            </p>

            <h2>
              {role === 'TECHNICIAN'
                ? 'Today’s field work'
                : role === 'CUSTOMER'
                ? 'Service requests'
                : 'Service operations'}
            </h2>

          </div>

          <div className="header-badge">
            <ShieldCheck size={16} />
            Secure session
          </div>

        </header>

        <Routes>

          <Route
            path="/"
            element={
              <Dashboard role={role} />
            }
          />

          <Route
            path="/work-orders"
            element={
              <WorkOrders role={role} />
            }
          />

          <Route
            path="/customers"
            element={<Customers />}
          />

          <Route
            path="/sites"
            element={<Sites />}
          />

          <Route
            path="*"
            element={
              <Navigate to="/" />
            }
          />

        </Routes>

      </main>

    </div>
  );
}

function Dashboard({
  role,
}: {
  role: Role;
}) {
  const [s, setS] =
    useState<any>();

  useEffect(() => {

    if (
      role === 'MANAGER' ||
      role === 'DISPATCHER'
    ) {
      api
        .get('/reports/summary')
        .then((r) =>
          setS(r.data)
        )
        .catch(() => {
          setS(undefined);
        });
    }

  }, [role]);

  if (role === 'CUSTOMER') {

    return (
      <div className="hero">

        <div>

          <p className="eyebrow">
            CUSTOMER PORTAL
          </p>

          <h1>
            Track every request
            without the phone calls.
          </h1>

          <p>
            Raise service requests,
            follow progress and review
            status history for your
            sites.
          </p>

        </div>

        <div className="hero-art">
          <Wrench size={54} />
        </div>

      </div>
    );
  }

  if (role === 'TECHNICIAN') {
    return (
      <WorkOrders
        role={role}
      />
    );
  }

  return (
    <>

      <div className="stats">

        {[
          ['New', s?.newCount],
          ['Assigned', s?.assigned],
          ['In progress', s?.inProgress],
          ['On hold', s?.onHold],
          ['Completed', s?.completed],
          ['Overdue', s?.overdue],
        ].map(
          ([a, b]) => (
            <div
              className="stat"
              key={a as string}
            >
              <span>{a}</span>
              <strong>
                {b ?? '—'}
              </strong>
            </div>
          )
        )}

      </div>

      <div className="grid2">

        <div className="panel">

          <div className="panel-head">

            <h3>
              SLA compliance
            </h3>

            <span className="score">
              {s?.slaCompliance ??
                '—'}
              %
            </span>

          </div>

          <div className="progress">

            <i
              style={{
                width: `${
                  s?.slaCompliance ||
                  0
                }%`,
              }}
            />

          </div>

        </div>

        <div className="panel">

          <h3>
            {role === 'DISPATCHER'
              ? 'Dispatcher view'
              : 'Manager view'}
          </h3>

          <p className="muted">

            {role === 'DISPATCHER'
              ? 'Use Work Orders to assign new jobs to technicians and monitor the dispatch board.'
              : 'Use Work Orders to dispatch jobs, monitor the Kanban board and review completed work before closing.'}

          </p>

        </div>

      </div>

    </>
  );
}

function WorkOrders({
  role,
}: {
  role: Role;
}) {
  const [rows, setRows] =
    useState<WO[]>([]);

  const [selected, setSelected] =
    useState<WO | null>(null);

  const [loading, setLoading] =
    useState(true);

  const [showCreateForm, setShowCreateForm] =
    useState(false);

  const [createTitle, setCreateTitle] =
    useState('');

  const [createDescription, setCreateDescription] =
    useState('');

  const [createPriority, setCreatePriority] =
    useState('HIGH');

  const [createCustomerId, setCreateCustomerId] =
    useState('');

  const [createSiteId, setCreateSiteId] =
    useState('');

  const [customerOptions, setCustomerOptions] =
    useState<any[]>([]);

  const [siteOptions, setSiteOptions] =
    useState<any[]>([]);

  const [createError, setCreateError] =
    useState('');

  const [creating, setCreating] =
    useState(false);

  async function loadSitesForCustomer(customerId: string) {
    if (!customerId) {
      setSiteOptions([]);
      setCreateSiteId('');
      return;
    }

    try {
      const response = await api.get(
        `/customers/${customerId}/sites`
      );

      setSiteOptions(response.data || []);
      setCreateSiteId('');
    } catch (error: any) {
      setSiteOptions([]);
      setCreateSiteId('');
      setCreateError(
        error?.response?.data?.message ||
          'Unable to load sites for the selected customer.'
      );
    }
  }

  async function submitCreateRequest() {
    setCreateError('');

    if (!createTitle.trim()) {
      setCreateError('Please enter a brief issue summary.');
      return;
    }

    if (!createCustomerId) {
      setCreateError('Please select a customer.');
      return;
    }

    if (!createSiteId) {
      setCreateError('Please select a site.');
      return;
    }

    setCreating(true);

    try {
      await api.post('/work-orders', {
        title: createTitle.trim(),
        description: createDescription.trim() || null,
        priority: createPriority,
        customerId: Number(createCustomerId),
        siteId: Number(createSiteId),
      });

      setCreateTitle('');
      setCreateDescription('');
      setCreatePriority('HIGH');
      setCreateCustomerId('');
      setCreateSiteId('');
      setSiteOptions([]);
      setShowCreateForm(false);
      await loadWorkOrders();
    } catch (error: any) {
      setCreateError(
        error?.response?.data?.message ||
          'Unable to create work order.'
      );
    } finally {
      setCreating(false);
    }
  }

  useEffect(() => {
    if (!showCreateForm) {
      return;
    }

    const customerFromLogin =
      localStorage.getItem('customerId') || '';

    setCreateError('');

    if (role === 'CUSTOMER') {
      setCustomerOptions([]);
      setCreateCustomerId(customerFromLogin);

      if (customerFromLogin) {
        loadSitesForCustomer(customerFromLogin);
      } else {
        setCreateError(
          'No customer ID is linked to this account.'
        );
      }

      return;
    }

    api
      .get('/customers')
      .then((response) => {
        setCustomerOptions(response.data || []);
        setCreateCustomerId('');
        setSiteOptions([]);
        setCreateSiteId('');
      })
      .catch((error: any) => {
        setCustomerOptions([]);
        setCreateCustomerId('');
        setSiteOptions([]);
        setCreateSiteId('');
        setCreateError(
          error?.response?.data?.message ||
            'Unable to load customers.'
        );
      });
  }, [role, showCreateForm]);

  async function loadWorkOrders() {

    setLoading(true);

    try {

      const response =
        await api.get(
          '/work-orders'
        );

      setRows(response.data);

    } catch (error: any) {

      alert(
        error?.response?.data
          ?.message ||
          'Unable to load work orders'
      );

    } finally {

      setLoading(false);

    }
  }

  useEffect(() => {
    loadWorkOrders();
  }, []);

  const statuses = [
    'NEW',
    'ASSIGNED',
    'IN_PROGRESS',
    'ON_HOLD',
    'COMPLETED',
  ];

  return (
    <>

      <div className="toolbar">

        <div>

          <h3>
            {role === 'TECHNICIAN'
              ? 'My Jobs'
              : 'Work orders'}
          </h3>

          <p className="muted">

            {loading
              ? 'Loading...'
              : `${rows.length} visible jobs`}

          </p>

        </div>

        {(
          role === 'MANAGER' ||
          role === 'DISPATCHER' ||
          role === 'CUSTOMER'
        ) && (

          <button
            className="primary"
            onClick={() => {
              setShowCreateForm(true);
              setCreateError('');
            }}
          >
            + New request
          </button>

        )}

      </div>

      <div
        style={{
          display: 'grid',
          gridTemplateColumns: showCreateForm
            ? 'minmax(0, 1fr) 390px'
            : 'minmax(0, 1fr)',
          gap: '20px',
          alignItems: 'start',
        }}
      >

        <div style={{ minWidth: 0 }}>

      {loading ? (

        <div className="panel">

          <p className="muted">
            Loading work orders...
          </p>

        </div>

      ) : rows.length === 0 ? (

        <div className="panel">

          <h3>
            No work orders
          </h3>

          <p className="muted">
            There are no work orders
            available for this account.
          </p>

        </div>

      ) : (

        <div className="board">

          {statuses.map(
            (status) => (

              <div
                className="column"
                key={status}
              >

                <div className="column-head">

                  <span>
                    {status
                      .replace(
                        '_',
                        ' '
                      )}
                  </span>

                  <b>
                    {
                      rows.filter(
                        (x) =>
                          x.status ===
                          status
                      ).length
                    }
                  </b>

                </div>

                {rows
                  .filter(
                    (x) =>
                      x.status ===
                      status
                  )
                  .map((w) => (

                    <button
                      className="card"
                      key={w.id}
                      onClick={() =>
                        setSelected(w)
                      }
                    >

                      <div className="card-top">

                        <b>
                          {w.code}
                        </b>

                        <span
                          className={`priority ${w.priority.toLowerCase()}`}
                        >
                          {w.priority}
                        </span>

                      </div>

                      <strong>
                        {w.title}
                      </strong>

                      <small>
                        {w.customerName}
                        {' · '}
                        {w.siteName}
                      </small>

                      <div className="card-foot">

                        <span>
                          {w.technicianName ||
                            'Unassigned'}
                        </span>

                        <span
                          className={
                            w.overdue
                              ? 'overdue'
                              : ''
                          }
                        >

                          {w.overdue
                            ? 'SLA breached'
                            : new Date(
                                w.slaDueAt
                              ).toLocaleString()}

                        </span>

                      </div>

                    </button>

                  ))}

              </div>

            )
          )}

        </div>

      )}

        </div>

        {showCreateForm && (

          <div
            className="panel"
            style={{
              position: 'sticky',
              top: '20px',
              minWidth: 0,
            }}
          >

            <div className="modal-head">

              <div>
                <p className="eyebrow">
                  SERVICE REQUEST
                </p>

                <h3>
                  Create new request
                </h3>
              </div>

              <button
                className="close"
                onClick={() =>
                  !creating &&
                  setShowCreateForm(false)
                }
                disabled={creating}
                aria-label="Close"
              >
                <X size={18} />
              </button>

            </div>

            <p className="muted">
              Enter the service issue and select the customer site that needs support.
            </p>

            <label>
              Customer *

              <select
                value={createCustomerId}
                onChange={(e) => {
                  const nextCustomerId = e.target.value;
                  setCreateCustomerId(nextCustomerId);
                  setCreateError('');

                  if (role !== 'CUSTOMER') {
                    loadSitesForCustomer(nextCustomerId);
                  }
                }}
                disabled={
                  role === 'CUSTOMER' ||
                  creating
                }
              >
                {role === 'CUSTOMER' ? (
                  <option value={createCustomerId}>
                    {createCustomerId
                      ? 'My customer account'
                      : 'Customer account not linked'}
                  </option>
                ) : (
                  <>
                    <option value="">
                      Select customer
                    </option>

                    {customerOptions.map(
                      (customer) => (
                        <option
                          key={customer.id}
                          value={customer.id}
                        >
                          {customer.name}
                          {customer.email
                            ? ` — ${customer.email}`
                            : ''}
                        </option>
                      )
                    )}
                  </>
                )}
              </select>
            </label>

            <label>
              Site *

              <select
                value={createSiteId}
                onChange={(e) => {
                  setCreateSiteId(e.target.value);
                  setCreateError('');
                }}
                disabled={
                  !createCustomerId ||
                  creating
                }
              >
                <option value="">
                  {createCustomerId
                    ? 'Select site'
                    : 'Select customer first'}
                </option>

                {siteOptions.map((site) => (
                  <option
                    key={site.id}
                    value={site.id}
                  >
                    {site.name}
                    {site.city
                      ? ` — ${site.city}`
                      : ''}
                  </option>
                ))}
              </select>
            </label>

            <label>
              Issue summary *

              <input
                value={createTitle}
                onChange={(e) => {
                  setCreateTitle(e.target.value);
                  setCreateError('');
                }}
                placeholder="Example: Air conditioner not working"
                disabled={creating}
              />
            </label>

            <label>
              Description

              <textarea
                value={createDescription}
                onChange={(e) =>
                  setCreateDescription(e.target.value)
                }
                placeholder="Describe the issue, symptoms, or service required..."
                rows={4}
                disabled={creating}
              />
            </label>

            <label>
              Priority

              <select
                value={createPriority}
                onChange={(e) =>
                  setCreatePriority(e.target.value)
                }
                disabled={creating}
              >
                <option value="LOW">
                  Low
                </option>
                <option value="MEDIUM">
                  Medium
                </option>
                <option value="HIGH">
                  High
                </option>
                <option value="URGENT">
                  Urgent
                </option>
              </select>
            </label>

            {createError && (
              <div className="error">
                {createError}
              </div>
            )}

            <div className="modal-actions">

              <button
                disabled={creating}
                onClick={() =>
                  setShowCreateForm(false)
                }
              >
                Cancel
              </button>

              <button
                className="primary"
                disabled={creating}
                onClick={submitCreateRequest}
              >
                {creating
                  ? 'Creating...'
                  : 'Create request'}
              </button>

            </div>

          </div>

        )}

      </div>

      {selected && (

        <Drawer
          w={selected}
          role={role}
          close={() =>
            setSelected(null)
          }
          refresh={async () => {

            const response =
              await api.get(
                '/work-orders'
              );

            setRows(
              response.data
            );

          }}
        />

      )}

    </>
  );
}

function Drawer({
  w,
  role,
  close,
  refresh,
}: {
  w: WO;
  role: Role;
  close: () => void;
  refresh: () => void;
}) {

  const [busy, setBusy] =
    useState(false);

  const [
    technicians,
    setTechnicians,
  ] = useState<any[]>([]);

  const [
    selectedTechnicianId,
    setSelectedTechnicianId,
  ] = useState('');

  const [
    assignmentLoading,
    setAssignmentLoading,
  ] = useState(false);

  const [
    assignmentError,
    setAssignmentError,
  ] = useState('');

  const [
    showTimeForm,
    setShowTimeForm,
  ] = useState(false);

  const [
    showCompleteForm,
    setShowCompleteForm,
  ] = useState(false);

  const [
    timeMinutes,
    setTimeMinutes,
  ] = useState('');

  const [
    timeNote,
    setTimeNote,
  ] = useState('');

  const [
    completionNote,
    setCompletionNote,
  ] = useState('');

  const [
    timeError,
    setTimeError,
  ] = useState('');

  const [
    completionError,
    setCompletionError,
  ] = useState('');

  useEffect(() => {
    if (
      (role === 'MANAGER' ||
        role === 'DISPATCHER') &&
      w.status === 'NEW'
    ) {
      setAssignmentError('');
      setSelectedTechnicianId('');

      api
        .get('/work-orders/technicians')
        .then((response) => {
          setTechnicians(response.data || []);
        })
        .catch((error: any) => {
          setTechnicians([]);
          setAssignmentError(
            error?.response?.data?.message ||
              'Unable to load technicians.'
          );
        });
    } else {
      setTechnicians([]);
      setSelectedTechnicianId('');
      setAssignmentError('');
    }
  }, [role, w.status]);

  async function assignTechnician() {
    setAssignmentError('');

    if (!selectedTechnicianId) {
      setAssignmentError(
        'Please select a technician.'
      );
      return;
    }

    setAssignmentLoading(true);

    try {
      await api.post(
        `/work-orders/${w.id}/assign`,
        {
          technicianId: Number(
            selectedTechnicianId
          ),
        }
      );

      await refresh();
      close();
    } catch (error: any) {
      setAssignmentError(
        error?.response?.data?.message ||
          'Unable to assign technician.'
      );
    } finally {
      setAssignmentLoading(false);
    }
  }

  async function action(
    status: string,
    note?: string
  ) {

    setBusy(true);

    try {

      await api.post(
        `/work-orders/${w.id}/status`,
        {
          status,
          note:
            note ||
            'Updated from KEYSTONE UI',
        }
      );

      await refresh();

      close();

    } catch (e: any) {

      alert(
        e?.response?.data?.message ||
          'Action failed'
      );

    } finally {

      setBusy(false);

    }
  }

  async function logTime() {

    setTimeError('');

    const minutes =
      Number(timeMinutes);

    if (
      !timeMinutes ||
      !Number.isInteger(
        minutes
      ) ||
      minutes <= 0
    ) {

      setTimeError(
        'Please enter valid time in minutes.'
      );

      return;
    }

    setBusy(true);

    try {

      await api.post(
        `/work-orders/${w.id}/time`,
        {
          minutes,
          note:
            timeNote.trim() ||
            null,
        }
      );

      setTimeMinutes('');
      setTimeNote('');
      setShowTimeForm(false);

      alert(
        'Time logged successfully.'
      );

    } catch (e: any) {

      setTimeError(
        e?.response?.data?.message ||
          'Unable to log time.'
      );

    } finally {

      setBusy(false);

    }
  }

  async function completeJob() {

    setCompletionError('');

    const note =
      completionNote.trim();

    if (!note) {

      setCompletionError(
        'Work completion note is required.'
      );

      return;
    }

    setBusy(true);

    try {

      await api.post(
        `/work-orders/${w.id}/status`,
        {
          status: 'COMPLETED',
          note,
        }
      );

      await refresh();

      close();

    } catch (e: any) {

      setCompletionError(
        e?.response?.data?.message ||
          'Unable to complete the job.'
      );

    } finally {

      setBusy(false);

    }
  }

  return (
    <div
      className="drawer-backdrop"
      onClick={close}
    >

      <div
        className="drawer"
        onClick={(e) =>
          e.stopPropagation()
        }
      >

        <button
          className="close"
          onClick={close}
          aria-label="Close"
        >
          <X size={18} />
        </button>

        <p className="eyebrow">
          {w.code}
        </p>

        <h2>
          {w.title}
        </h2>

        <div className="tag-row">

          <span className="tag">
            {w.priority}
          </span>

          <span className="tag">
            {w.status}
          </span>

          {w.overdue && (

            <span className="tag danger">
              SLA BREACH
            </span>

          )}

        </div>

        <div className="detail">

          <b>Customer</b>

          <span>
            {w.customerName}
          </span>

          <b>Site</b>

          <span>
            {w.siteName}
          </span>

          <b>Technician</b>

          <span>
            {w.technicianName ||
              'Unassigned'}
          </span>

          <b>SLA due</b>

          <span>
            {new Date(
              w.slaDueAt
            ).toLocaleString()}
          </span>

        </div>

        {(role === 'MANAGER' ||
          role === 'DISPATCHER') &&
          w.status === 'NEW' && (
            <div className="assignment-panel">
              <div className="assignment-header">
                <div>
                  <p className="eyebrow">
                    DISPATCH
                  </p>
                  <h3>
                    Assign Technician
                  </h3>
                </div>

                <UserCheck size={22} />
              </div>

              <p className="muted">
                Select an enabled technician
                for this work order.
              </p>

              <label>
                Technician *

                <select
                  value={selectedTechnicianId}
                  onChange={(e) =>
                    setSelectedTechnicianId(
                      e.target.value
                    )
                  }
                  disabled={
                    assignmentLoading
                  }
                >
                  <option value="">
                    Select technician
                  </option>

                  {technicians.map(
                    (technician) => (
                      <option
                        key={technician.id}
                        value={technician.id}
                      >
                        {technician.name}
                        {' — '}
                        {technician.email}
                      </option>
                    )
                  )}
                </select>
              </label>

              {technicians.length === 0 &&
                !assignmentError && (
                  <p className="muted">
                    No enabled technicians
                    are available.
                  </p>
                )}

              {assignmentError && (
                <div className="error">
                  {assignmentError}
                </div>
              )}

              <button
                className="primary full"
                disabled={
                  assignmentLoading ||
                  !selectedTechnicianId
                }
                onClick={
                  assignTechnician
                }
              >
                {assignmentLoading
                  ? 'Assigning...'
                  : 'Assign Technician'}
              </button>
            </div>
          )}

        <h3>
          Status history
        </h3>

        <div className="timeline">

          {w.history.map(
            (
              h: any,
              i: number
            ) => (

              <div key={i}>

                <span />

                <div>

                  <b>
                    {h.to}
                  </b>

                  <small>
                    {h.by}
                    {' · '}
                    {new Date(
                      h.at
                    ).toLocaleString()}
                  </small>

                  {h.note && (
                    <p>
                      {h.note}
                    </p>
                  )}

                </div>

              </div>

            )
          )}

        </div>

        {role === 'TECHNICIAN' && (

          <div className="actions">

            {w.status ===
              'ASSIGNED' && (

              <button
                disabled={busy}
                onClick={() =>
                  action(
                    'IN_PROGRESS',
                    'Job started by technician'
                  )
                }
              >

                <PlayCircle size={17} />

                Start job

              </button>

            )}

            {w.status ===
              'IN_PROGRESS' && (

              <>

                <button
                  disabled={busy}
                  onClick={() =>
                    action(
                      'ON_HOLD',
                      'Job placed on hold by technician'
                    )
                  }
                >

                  <PauseCircle
                    size={17}
                  />

                  Hold

                </button>

                <button
                  disabled={busy}
                  onClick={() =>
                    setShowTimeForm(
                      true
                    )
                  }
                >

                  <Clock size={17} />

                  Log Time

                </button>

                <button
                  disabled={busy}
                  className="primary"
                  onClick={() =>
                    setShowCompleteForm(
                      true
                    )
                  }
                >

                  <CheckCircle2
                    size={17}
                  />

                  Complete Job

                </button>

              </>

            )}

            {w.status ===
              'ON_HOLD' && (

              <button
                disabled={busy}
                className="primary"
                onClick={() =>
                  action(
                    'IN_PROGRESS',
                    'Job resumed by technician'
                  )
                }
              >

                <PlayCircle size={17} />

                Resume

              </button>

            )}

            {w.status ===
              'COMPLETED' && (

              <div className="completion-status">

                <CheckCircle2
                  size={20}
                />

                <div>

                  <b>
                    Job completed
                  </b>

                  <small>
                    Awaiting manager
                    review and sign-off.
                  </small>

                </div>

              </div>

            )}

            {w.status ===
              'CLOSED' && (

              <div className="completion-status">

                <CheckCircle2
                  size={20}
                />

                <div>

                  <b>
                    Job closed
                  </b>

                  <small>
                    Manager has signed
                    off this work order.
                  </small>

                </div>

              </div>

            )}

          </div>

        )}

        {role === 'MANAGER' &&
          w.status ===
            'COMPLETED' && (

            <div className="manager-review">

              <div className="review-box">

                <CheckCircle2
                  size={22}
                />

                <div>

                  <b>
                    Technician completed
                    this job
                  </b>

                  <p>
                    Review the completion
                    note and status history
                    before closing the work
                    order.
                  </p>

                </div>

              </div>

              <button
                disabled={busy}
                className="primary full"
                onClick={() =>
                  action(
                    'CLOSED',
                    'Reviewed and closed by manager'
                  )
                }
              >
                Close & sign off
              </button>

            </div>

          )}

        {showTimeForm && (

          <div className="modal-overlay">

            <div
              className="modal-card"
              onClick={(e) =>
                e.stopPropagation()
              }
            >

              <div className="modal-head">

                <div>

                  <p className="eyebrow">
                    TIME TRACKING
                  </p>

                  <h3>
                    Log time spent
                  </h3>

                </div>

                <button
                  className="close"
                  onClick={() =>
                    setShowTimeForm(
                      false
                    )
                  }
                >
                  <X size={18} />
                </button>

              </div>

              <p className="muted">
                Record the time spent
                working on this job.
              </p>

              <label>
                Minutes *

                <input
                  type="number"
                  min="1"
                  value={timeMinutes}
                  onChange={(e) =>
                    setTimeMinutes(
                      e.target.value
                    )
                  }
                  placeholder="Example: 45"
                />

              </label>

              <label>
                Time note

                <textarea
                  value={timeNote}
                  onChange={(e) =>
                    setTimeNote(
                      e.target.value
                    )
                  }
                  placeholder="What work was performed during this time?"
                  rows={4}
                />

              </label>

              {timeError && (

                <div className="error">
                  {timeError}
                </div>

              )}

              <div className="modal-actions">

                <button
                  disabled={busy}
                  onClick={() =>
                    setShowTimeForm(
                      false
                    )
                  }
                >
                  Cancel
                </button>

                <button
                  disabled={busy}
                  className="primary"
                  onClick={logTime}
                >
                  {busy
                    ? 'Saving...'
                    : 'Save Time'}
                </button>

              </div>

            </div>

          </div>

        )}

        {showCompleteForm && (

          <div className="modal-overlay">

            <div
              className="modal-card"
              onClick={(e) =>
                e.stopPropagation()
              }
            >

              <div className="modal-head">

                <div>

                  <p className="eyebrow">
                    JOB COMPLETION
                  </p>

                  <h3>
                    Complete work order
                  </h3>

                </div>

                <button
                  className="close"
                  onClick={() =>
                    setShowCompleteForm(
                      false
                    )
                  }
                >
                  <X size={18} />
                </button>

              </div>

              <div className="requirement-box">

                <div className="requirement">

                  <CheckCircle2
                    size={18}
                  />

                  <span>
                    Completion note required
                  </span>

                </div>

                <div className="requirement">

                  <Clock size={18} />

                  <span>
                    Time must be logged
                  </span>

                </div>

                <div className="requirement">

                  <Wrench size={18} />

                  <span>
                    Parts are optional
                  </span>

                </div>

              </div>

              <label>

                Work completion note *

                <textarea
                  value={
                    completionNote
                  }
                  onChange={(e) =>
                    setCompletionNote(
                      e.target.value
                    )
                  }
                  placeholder="Describe the work completed, issue resolved, tests performed, and final result..."
                  rows={6}
                />

              </label>

              {completionError && (

                <div className="error">

                  {completionError}

                </div>

              )}

              <div className="modal-actions">

                <button
                  disabled={busy}
                  onClick={() =>
                    setShowCompleteForm(
                      false
                    )
                  }
                >
                  Cancel
                </button>

                <button
                  disabled={
                    busy ||
                    !completionNote.trim()
                  }
                  className="primary"
                  onClick={
                    completeJob
                  }
                >

                  {busy
                    ? 'Completing...'
                    : 'Complete Job'}

                </button>

              </div>

            </div>

          </div>

        )}

      </div>

    </div>
  );
}

function Customers() {

  const [rows, setRows] =
    useState<any[]>([]);

  const [loading, setLoading] =
    useState(true);

  useEffect(() => {

    api
      .get('/customers')
      .then((r) =>
        setRows(r.data)
      )
      .catch(() => {
        setRows([]);
      })
      .finally(() =>
        setLoading(false)
      );

  }, []);

  return (
    <div className="panel">

      <div className="panel-head">

        <div>

          <h3>
            Customers
          </h3>

          <p className="muted">
            Organisations served by
            Meridian
          </p>

        </div>

      </div>

      {loading ? (

        <p className="muted">
          Loading customers...
        </p>

      ) : (

        <table>

          <thead>

            <tr>
              <th>Name</th>
              <th>Email</th>
              <th>Phone</th>
            </tr>

          </thead>

          <tbody>

            {rows.map((x) => (

              <tr key={x.id}>

                <td>
                  <b>
                    {x.name}
                  </b>
                </td>

                <td>
                  {x.email ||
                    '—'}
                </td>

                <td>
                  {x.phone ||
                    '—'}
                </td>

              </tr>

            ))}

          </tbody>

        </table>

      )}

    </div>
  );
}

function Sites() {

  const [rows, setRows] =
    useState<any[]>([]);

  const [loading, setLoading] =
    useState(true);

  useEffect(() => {

    api
      .get('/customers')
      .then(async (r) => {

        const all =
          (
            await Promise.all(
              r.data.map(
                (c: any) =>
                  api.get(
                    `/customers/${c.id}/sites`
                  )
              )
            )
          ).flatMap(
            (x: any) =>
              x.data
          );

        setRows(all);

      })
      .catch(() => {

        setRows([]);

      })
      .finally(() =>
        setLoading(false)
      );

  }, []);

  return (
    <div className="panel">

      <h3>
        Sites
      </h3>

      {loading ? (

        <p className="muted">
          Loading sites...
        </p>

      ) : (

        <table>

          <thead>

            <tr>

              <th>Site</th>
              <th>City</th>
              <th>Address</th>
              <th>Customer</th>

            </tr>

          </thead>

          <tbody>

            {rows.map((x) => (

              <tr key={x.id}>

                <td>
                  <b>
                    {x.name}
                  </b>
                </td>

                <td>
                  {x.city}
                </td>

                <td>
                  {x.address}
                </td>

                <td>
                  {x.customerId}
                </td>

              </tr>

            ))}

          </tbody>

        </table>

      )}

    </div>
  );
}

export default App;