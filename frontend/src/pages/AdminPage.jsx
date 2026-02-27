import React, { useState } from 'react';
import ExamList from '../components/ExamList';
import ExamForm from '../components/ExamForm';
import { createExam, updateExam, deleteExam } from '../api/examApi';

/**
 * Admin page at /admin — not linked from the main nav.
 * Provides full CRUD access to exams.
 */
function AdminPage() {
  const [editingExam, setEditingExam] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [refreshKey, setRefreshKey] = useState(0);
  const [statusMsg, setStatusMsg] = useState(null);

  const refresh = () => setRefreshKey((k) => k + 1);

  const handleAdd = () => {
    setEditingExam(null);
    setShowForm(true);
  };

  const handleEdit = (exam) => {
    setEditingExam(exam);
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this exam? This cannot be undone.')) return;
    try {
      await deleteExam(id);
      setStatusMsg('Exam deleted.');
      refresh();
    } catch {
      setStatusMsg('Failed to delete exam.');
    }
  };

  const handleSave = async (formData) => {
    try {
      if (editingExam) {
        await updateExam(editingExam.id, formData);
        setStatusMsg('Exam updated successfully.');
      } else {
        await createExam(formData);
        setStatusMsg('Exam added successfully.');
      }
      setShowForm(false);
      setEditingExam(null);
      refresh();
    } catch {
      setStatusMsg('Failed to save exam. Check that all required fields are filled.');
    }
  };

  const handleCancel = () => {
    setShowForm(false);
    setEditingExam(null);
  };

  return (
    <div className="page admin-page">
      <header className="page-header">
        <h1>Admin Panel</h1>
        <p className="tagline">Manage the MSOE exam schedule. Changes are live immediately.</p>
      </header>

      <div className="admin-toolbar">
        <button className="btn btn-primary" onClick={handleAdd}>
          + Add Exam
        </button>
        {statusMsg && (
          <span className="status-badge">{statusMsg}</span>
        )}
      </div>

      <ExamList
        key={refreshKey}
        showEdit
        onEdit={handleEdit}
        onDelete={handleDelete}
      />

      {showForm && (
        <ExamForm
          exam={editingExam}
          onSave={handleSave}
          onCancel={handleCancel}
        />
      )}
    </div>
  );
}

export default AdminPage;
