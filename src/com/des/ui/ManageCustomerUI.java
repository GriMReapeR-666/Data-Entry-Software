package com.des.ui;

import java.awt.BorderLayout;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.des.event.EventManager;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

public class ManageCustomerUI extends JFrame
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -5317819952601152864L;
	private JPanel				contentPane;
	private JTable				table;
	private JButton				btnUpdate;
	private JButton				btnDelete;
	private JPanel				southPanel;
	
	
	/**
	 * Create the frame.
	 */
	public ManageCustomerUI()
	{
		setTitle("Manage Customer");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
		setSize(screenSize);
		setVisible(true);
		setResizable(false);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(15, 15));
		setContentPane(contentPane);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(null);			   

		southPanel = new JPanel();
		contentPane.add(southPanel, BorderLayout.SOUTH);
		southPanel.setLayout(new GridLayout(1, 0, 0, 0));

		setButtons(panel, southPanel);
		setGridView(panel);

		// Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(table);
		// Add the scroll pane to this panel.
		getContentPane().add(scrollPane);

		HeaderPanelUI hpu = new HeaderPanelUI();
		contentPane.add(hpu, BorderLayout.NORTH);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e)
			{
				Frame[] f = getFrames();
				for (Frame frame : getFrames())
				{
					if (frame instanceof ManageCustomerUI == false)
					{
						frame.dispose();
					}
				}
			}
		});
	}

	private void setGridView(JPanel panel)
	{
		List<Map<String, Object>> customers;
		try
		{
			customers = EventManager.getAllCustomerList();
			// create model for jtable
			DefaultTableModel model = new DefaultTableModel(new String[] { "Id", "Full Name", "Gender",
					"Contact Number", "Email", "Maritial Status", "Date Of Birth", "Anniversary" }, 0) {
				@Override
				public boolean isCellEditable(int row, int column)
				{
					return false;
				}
			};

			if (!customers.isEmpty())
			{
				for (Map<String, Object> map : customers)
				{
					model.addRow(new Object[] { map.get("id"), map.get("fullName"), map.get("gender"),
							map.get("contactNo"), map.get("email"), map.get("maritialStatus"), map.get("dob"),
							map.get("anniversary"), "Update" });
				}
			}

			table = new JTable(model);
			table.setPreferredScrollableViewportSize(new Dimension(500, 70));
			table.setFillsViewportHeight(true);

			DefaultTableCellRenderer stringRenderer = (DefaultTableCellRenderer) table.getDefaultRenderer(String.class);
			stringRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		}
		catch (ClassNotFoundException e)
		{
			JOptionPane.showMessageDialog(null, "Database not available , Please contact System Admin");
		}
		catch (SQLException e)
		{
			JOptionPane.showMessageDialog(null, "Database not available , Please contact System Admin");
		}

	}

	private void setButtons(JPanel panel, JPanel southPanel)
	{
		btnUpdate = new JButton("Update");
		btnUpdate.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					setDefaultButtonUpdate();
				}
			}
		});
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event)
			{
				if (table.getSelectedRow() == -1 || table.getSelectedRows().length > 1)
				{
					JOptionPane.showMessageDialog(null, "Please select appropriate row");
				}
				else
				{
					Integer id = (Integer) table.getValueAt(table.getSelectedRow(), 0);
					close();
					UpdateCustomerUI customerUpdateUI = new UpdateCustomerUI();
					customerUpdateUI.loadData(id);
					customerUpdateUI.setVisible(true);
				}
			}
		});
		southPanel.add(btnUpdate);

		btnDelete = new JButton("Delete");
		btnDelete.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					setDefaultButtonDelete();
				}
			}
		});
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
				if (table.getSelectedRow() == -1 || table.getSelectedRows().length > 1)
				{
					JOptionPane.showMessageDialog(null, "Please select only one row");
				}
				else
				{
					int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete?");
					if (dialogResult == JOptionPane.YES_OPTION)
					{
						Integer id = (Integer) table.getValueAt(table.getSelectedRow(), 0);
						try
						{
							EventManager.deleteCustomer(id);
							// refreshing table to adopt changes
							List<Map<String, Object>> customers = EventManager.getAllCustomerList();
							DefaultTableModel model = new DefaultTableModel(new String[] { "Id", "Full Name", "Gender",
									"Contact Number", "Email", "Maritial Status", "Date Of Birth", "Anniversary" }, 0);
							if (!customers.isEmpty())
							{
								for (Map<String, Object> map : customers)
								{
									model.addRow(new Object[] { map.get("id"), map.get("fullName"), map.get("gender"),
											map.get("contactNo"), map.get("email"), map.get("maritialStatus"),
											map.get("dob"), map.get("anniversary"), "Update" });
								}
							}

							table.setModel(model);
						}
						catch (ClassNotFoundException e)
						{
							JOptionPane.showMessageDialog(null, "Database not available , Please contact System Admin");
						}
						catch (SQLException e)
						{
							JOptionPane.showMessageDialog(null, "Database not available , Please contact System Admin");
						}
					}
				}
			}
		});
		southPanel.add(btnDelete);

	}

	public void setDefaultButtonUpdate()
	{
		super.getRootPane().setDefaultButton(btnUpdate);
	}

	public void setDefaultButtonDelete()
	{
		super.getRootPane().setDefaultButton(btnDelete);
	}

	public void close()
	{
		super.setVisible(false);
	}
}